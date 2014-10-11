package servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.WarController;

@WebServlet(name = "WarServlet" , urlPatterns = "/WarServlet")
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
		WarController controller = (WarController) getServletContext().getAttribute("Controller");
		resp.setContentType("text/html;charset=UTF-8");
		PrintWriter out = resp.getWriter();
		int type = Integer.parseInt(req.getParameter("RequestType"));
		
		switch (type) {
		case ADD_IRON_DOME: {
			String id = controller.addIronDome();
			out.append("<html> " + id == null ? id + " added successfully"
					: "Iron dome not added" + "</html>");
			break;
		}
		case ADD_LAUNCHER_DESTRUCTOR: {
			String id = controller.addDefenseLauncherDestructor((String) req
					.getParameter("DestType"));
			out.append("<html> " + id == null ? id + " added successfully"
					: "Launcher destructor not added" + "</html>");
			break;
		}
		case INTERCEPT_MISSILE: {
			controller.interceptGivenMissile((String) req.getParameter("Mid"));
			out.append("<html> Trying to intercept " + req.getParameter("Mid")
					+ "</html>");
			break;
		}
		case INTERCEPT_LAUNCHER: {
			controller.interceptGivenLauncher((String) req.getParameter("lid"));
			out.append("<html> Trying to intercept " + req.getParameter("lid")
					+ "</html>");
			break;
		}
		}
	}

	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	};
}
