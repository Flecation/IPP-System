package IPPSystem.Utils;

import org.mindrot.jbcrypt.BCrypt;

public class passwordCrafting {
    public static String hashPassword(String password){
        return BCrypt.hashpw(password,BCrypt.gensalt(12));
    }

    public static boolean checkPassword(String inputPw,String realPw){
        return BCrypt.checkpw(inputPw, realPw);
    }
}
