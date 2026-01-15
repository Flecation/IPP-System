package IPPSystem.Utils;

import IPPSystem.Models.users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class session {

        private static session instance;

        private users userLogin;

        private session(){}

        public static session getInstance(){
            if (instance == null) {
                instance = new session();
            }
            return instance;
        }

        public users getUser(){
            return userLogin;
        }

        public void setUser(users users){
            this.userLogin = users;
        }

        public void clear(){
            userLogin = null;
        }
}
