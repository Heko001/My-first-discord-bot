package MyBot;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class KickCommand extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if (event.getName().equals("kick")) {
            Member member = event.getOption("user").getAsMember();
            String reason = event.getOption("reason").getAsString();

            if (event.getGuild().getSelfMember().hasPermission(Permission.KICK_MEMBERS)) {
                if (member != null) {
                    if (!member.equals(event.getGuild().getSelfMember())) {
                        if (event.getGuild().getSelfMember().canInteract(member)) {
                            // Überprüfen, ob der Bot eine höhere Rolle als der Benutzer hat
                            if (event.getGuild().getSelfMember().getRoles().stream().anyMatch(role -> role.getPosition() > member.getRoles().get(0).getPosition())) {
                                event.reply("Kicking " + member.getAsMention() + " for the reason: " + reason).queue(
                                        success -> {
                                            event.getGuild().kick(member, reason).queue(
                                                    unused -> event.getHook().sendMessage("Successfully kicked " + member.getAsMention() + ".").setEphemeral(true).queue(),
                                                    error -> event.getHook().sendMessage("Failed to kick " + member.getAsMention() + ". Check my permissions and try again.").queue()
                                            );
                                        },
                                        error -> event.getHook().sendMessage("Failed to kick " + member.getAsMention() + ". Check my permissions and try again.").queue()
                                );
                            } else {
                                event.reply("I cannot kick a member with higher or equal highest role than myself!").queue();
                            }
                        } else {
                            event.reply("I cannot kick a member with higher or equal highest role than myself!").queue();
                        }
                    } else {
                        event.reply("I cannot kick myself!").queue();
                    }
                } else {
                    event.reply("Member not found!").queue();
                }
            } else {
                event.reply("I do not have the necessary permissions to kick members.").queue();
            }
        }
    }
}
