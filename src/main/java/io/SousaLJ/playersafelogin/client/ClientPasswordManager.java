package io.SousaLJ.playersafelogin.client;

import io.SousaLJ.playersafelogin.PlayerSafeLogin;
import io.SousaLJ.playersafelogin.util.SecurityConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.io.IOException;

/**
 * Classe responsável por gerenciar a senha do jogador no cliente.
 */
public class ClientPasswordManager {

    private static final Path PASSWORD_FILE = Minecraft.getInstance().gameDirectory.toPath().resolve("player_safelogin.dat");
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * Salva o hash da senha no arquivo local.
     * @param hash Hash da senha em formato hexadecimal.
     * @param password Senha original (para validação futura se necessário).
     */
    public static boolean savePassword(String hash, String password) {
        try {
            if(validatePassword(password, hash)) {
                Files.writeString(PASSWORD_FILE, hash, StandardCharsets.UTF_8);
                PlayerSafeLogin.LOGGER.info("Senha salva com sucesso.");
                return true;
            } else {
                PlayerSafeLogin.LOGGER.error(Component.translatable("playersafelogin.gui.invalid_password_hash") + ": " + password);
                return false;
            }

        } catch (IOException e) {
            PlayerSafeLogin.LOGGER.error("Erro ao salvar senha: ", e);
            return false;
        }
    }

    /**
     * Carrega a senha salva (hash) do arquivo.
     * @return Hash da senha se existir, ou null se não existir.
     */
    public static String loadPassword() {
        try {
            if (Files.exists(PASSWORD_FILE)) {
                return Files.readString(PASSWORD_FILE, StandardCharsets.UTF_8).trim();
            }
        } catch (IOException e) {
            PlayerSafeLogin.LOGGER.error("Erro ao carregar senha: ", e);
        }
        return null;
    }

    private static boolean validatePassword(String password, String hash) {
        String hashedPassword = hashPassword(password);
        return MessageDigest.isEqual(hashedPassword.getBytes(), hash.getBytes());
    }

    /**
     * Verifica se existe uma senha salva.
     * @return True se o arquivo de senha existe.
     */
    public static boolean hasSavedPassword() {
        return Files.exists(PASSWORD_FILE);
    }

    /**
     * Gera uma senha aleatória respeitando os limites de tamanho definidos.
     * @return Uma senha aleatória segura.
     */
    public static String generateRandomPassword() {
        int length = RANDOM.nextInt(SecurityConstants.MAX_PASSWORD_LENGTH - SecurityConstants.MIN_PASSWORD_LENGTH + 1) + SecurityConstants.MIN_PASSWORD_LENGTH;
        StringBuilder sb = new StringBuilder(length);
        String chars = SecurityConstants.ALLOWED_CHARACTERS;
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(RANDOM.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * Gera o hash SHA-256 de uma senha.
     * @param password Senha original.
     * @return Hash SHA-256 em formato hexadecimal.
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 não disponível!", e);
        }
    }

    /**
     * Converte bytes para string hexadecimal.
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    public static void deleteSavedPassword() {
        try {
            Files.deleteIfExists(PASSWORD_FILE);
            PlayerSafeLogin.LOGGER.info("Senha local deletada.");
        } catch (IOException e) {
            PlayerSafeLogin.LOGGER.error("Erro ao deletar senha local: ", e);
        }
    }
}