package main;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import utils.WarXMLReader;
import model.War;

@WebServlet(name = "WarServlet", urlPatterns = "/HandleRequest")
public class WarServlet extends HttpServlet {
    private static final long serialVersionUID = 2719968569903572324L;
    private static final int ADD_IRON_DOME = 0;
    private static final int ADD_LAUNCHER_DESTRUCTOR = 1;
    private static final int INTERCEPT_MISSILE = 2;
    private static final int INTERCEPT_LAUNCHER = 3;

    public WarServlet() {
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	    throws ServletException, IOException {
	resp.setContentType("text/html;charset=UTF-8");
	PrintWriter out = resp.getWriter();
	War w = (War) getServletContext().getAttribute("war");
	if (w == null) {
	    try {
		getServletContext().setAttribute("war",
			w = new War("ServletWar", false));
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	    w.start();
	}
	int type = Integer.parseInt(req.getParameter("RequestType"));

	// System.out.println(c);
	switch (type) {
	case ADD_IRON_DOME: {
	    String id = w.addIronDome();
	    out.append("<html> "
		    + (id != null ? id + " added successfully"
			    : "Iron dome not added") + "</html>");
	    out.println("\n<a href=\"/WarServlet\"><input type=\"button\" value=\"Back to main menu\"></a>");
	    break;
	}
	case ADD_LAUNCHER_DESTRUCTOR: {
	    String id =
		    w.addDefenseLauncherDestructor((String) req
			    .getParameter("DestType"));
	    out.append("<html> "
		    + (id != null ? id + " added successfully"
			    : "Launcher destructor not added") + "</html>");
	    out.println("\n<a href=\"/WarServlet\"><input type=\"button\" value=\"Back to main menu\"></a>");
	    break;
	}
	case INTERCEPT_MISSILE: {
	    getServletContext().getRequestDispatcher("/InterceptMissile")
		    .forward(req, resp);
	    break;
	}
	case INTERCEPT_LAUNCHER: {
	    getServletContext().getRequestDispatcher("/InterceptLauncher")
		    .forward(req, resp);
	    break;
	}
	}
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
	    throws ServletException, IOException {
	doPost(req, resp);
    }
}
