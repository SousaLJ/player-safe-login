package io.SousaLJ.playersafelogin.storage;

import java.util.UUID;

public interface StorageProvider {
    boolean setupPassword(UUID playerId, String hashedPassword);
    boolean validatePassword(UUID playerId, String hashedPassword);
    boolean resetPassword(UUID playerId);
    boolean requiresInitialSetup(UUID playerId);
}
