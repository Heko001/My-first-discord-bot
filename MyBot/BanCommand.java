package MyBot;


import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class BanCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("ban")) {
            Member member = event.getOption("user").getAsMember();
            String reason = event.getOption("reason").getAsString();

            if (event.getGuild().getSelfMember().hasPermission(Permission.BAN_MEMBERS)) {
                if (member != null) {
                    if (!member.equals(event.getGuild().getSelfMember())) {
                        if (event.getGuild().getSelfMember().canInteract(member)) {
                            // Überprüfen, ob der Bot eine höhere Rolle als der Benutzer hat
                            if (event.getGuild().getSelfMember().getRoles().stream().anyMatch(role -> role.getPosition() > member.getRoles().get(0).getPosition())) {
                                int daysToDeleteMessages = 0; // Standardmäßig 0 (keine Nachrichten löschen)

                                if (event.getOption("delete_days") != null) {
                                    try {
                                        daysToDeleteMessages = Integer.parseInt(event.getOption("delete_days").getAsString());
                                    } catch (NumberFormatException e) {
                                        event.reply("Invalid value for days to delete messages. Please provide a valid number or leave it empty.").queue();
                                        return;
                                    }
                                }

                                int finalDaysToDeleteMessages = daysToDeleteMessages;
                                event.reply("Banning " + member.getAsMention() + " for the reason: " + reason).queue(
                                        success -> {
                                            event.getGuild().ban(member, finalDaysToDeleteMessages, TimeUnit.DAYS).queue(
                                                    unused -> event.getHook().sendMessage("Successfully banned " + member.getAsMention() + ".").setEphemeral(true).queue(),
                                                    error -> event.getHook().sendMessage("Failed to ban " + member.getAsMention() + ". Check my permissions and try again.").queue()
                                            );
                                        },
                                        error -> event.getHook().sendMessage("Failed to ban " + member.getAsMention() + ". Check my permissions and try again.").queue()
                                );
                            } else {
                                event.reply("I cannot ban a member with higher or equal highest role than myself!").queue();
                            }
                        } else {
                            event.reply("I cannot ban a member with higher or equal highest role than myself!").queue();
                        }
                    } else {
                        event.reply("I cannot ban myself!").queue();
                    }
                } else {
                    event.reply("Member not found!").queue();
                }
            } else {
                event.reply("I do not have the necessary permissions to ban members.").queue();
            }
        }
    }
}
