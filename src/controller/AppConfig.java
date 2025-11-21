package controller;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import model.DatabaseUtils;

@WebListener
public class AppConfig implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();

        String url = ctx.getInitParameter("jdbcUrl");
        String user = ctx.getInitParameter("jdbcUser");
        String pass = ctx.getInitParameter("jdbcPassword");

        DatabaseUtils.configure(url, user, pass);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
