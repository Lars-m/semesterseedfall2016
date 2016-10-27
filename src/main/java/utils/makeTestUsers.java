package utils;

import entity.Role;
import entity.User;
import facades.UserFacade;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class makeTestUsers {

  //Only for initial testing REMOVE BEFORE PRODUCTION
  public static void main(String[] args) {
    EntityManager em = Persistence.createEntityManagerFactory("pu").createEntityManager();
    try {
      System.out.println("Creating TEST Users -- REMOVE THIS CODE");
      em.getTransaction().begin();
      Role userRole = new Role("User");
      Role adminRole = new Role("Admin"); 
      User user = new User("user", "test");
      user.addRole(userRole);
      User admin = new User("admin", "test");
      admin.addRole(adminRole);
      User both = new User("user_admin", "test");
      both.addRole(userRole);
      both.addRole(adminRole);
      em.persist(userRole);
      em.persist(adminRole);
      em.persist(user);
      em.persist(admin);
      em.persist(both);
      em.getTransaction().commit();
      System.out.println("CReated TEST Users -- REMOVE THIS CODE");

    } catch (Exception ex) {
      Logger.getLogger(UserFacade.class.getName()).log(Level.SEVERE, null, ex);
      em.getTransaction().rollback();
    } finally {
      em.close();
    }
  }
}
