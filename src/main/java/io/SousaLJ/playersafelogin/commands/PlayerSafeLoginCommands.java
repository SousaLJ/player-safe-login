package io.SousaLJ.playersafelogin.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.neoforge.event.RegisterCommandsEvent;


public class PlayerSafeLoginCommands {

    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        DeletePlayerAccount.register(dispatcher);
        ChangePasswordCommand.register(dispatcher);
    }
}
