package io.SousaLJ.playersafelogin.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.SousaLJ.playersafelogin.PlayerSafeLogin;
import io.SousaLJ.playersafelogin.network.PlayerSafeLoginPacketHandler;
import io.SousaLJ.playersafelogin.network.payloads.ResetPasswordPayload;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Supplier;

public class ChangePasswordCommand implements Command<CommandSourceStack> {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("playersafelogin")
                .then(Commands.literal("resetpassword")
                .then(Commands.argument("newPassword", StringArgumentType.string())
                        .executes(new ChangePasswordCommand())
                )
        ));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        if (player == null) {
            context.getSource().sendFailure(Component.translatable("playersafelogin.command.reset_password.not_player"));
            return 0;
        }
        String newPassword = context.getArgument("newPassword", String.class);

        PlayerSafeLogin.getStorage().resetPassword(player.getUUID());

        ResetPasswordPayload payload = new ResetPasswordPayload(player.getUUID(), newPassword);
        PlayerSafeLoginPacketHandler.sendToClient(payload, player);
        context.getSource().sendSystemMessage(Component.translatable("playersafelogin.command.reset_password.success"));
        return 1;
    }
}
