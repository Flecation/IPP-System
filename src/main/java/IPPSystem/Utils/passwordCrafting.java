package IPPSystem.Utils;

import org.mindrot.jbcrypt.BCrypt;

public final class passwordCrafting {

    private passwordCrafting() {}

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }

    public static boolean checkPassword(String inputPw, String realPw) {
        if (realPw == null || realPw.isEmpty()) {
            return false;
        }
        return BCrypt.checkpw(inputPw, realPw);
    }
}
