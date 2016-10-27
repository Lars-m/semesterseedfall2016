package security;

import deployment.DeploymentConfiguration;
import facades.UserFacade;
import javax.persistence.Persistence;

/**
 *
 * @author lam
 */
public class UserFacadeFactory {
    private static final IUserFacade instance = 
            new UserFacade(Persistence.createEntityManagerFactory(DeploymentConfiguration.PU_NAME));
    public static IUserFacade getInstance(){
        return instance;
    }
}
