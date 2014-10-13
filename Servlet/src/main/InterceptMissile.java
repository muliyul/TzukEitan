package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.War;

/**
 * Servlet implementation class InterceptMissile
 */
@WebServlet("/InterceptMissile")
public class InterceptMissile extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public InterceptMissile() {
	super();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException {
	doPost(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doPost(HttpServletRequest request,
	    HttpServletResponse response) throws ServletException, IOException {
	War w = (War) getServletContext().getAttribute("war");
	PrintWriter out = response.getWriter();
	String mid = request.getParameter("mid");
	out.println("<html>");
	Vector<String> strings = w.getAllDuringFlyMissilesIds();
	if (mid == null) {
	    if (strings != null)
		if (strings.size() > 0) {
		    out.println("<form name=\"interceptMissileForm\" action=\"/WarServlet/InterceptMissile\" method=\"POST\">");
		    out.println("Missiles in air: ");
		    out.println("<select name=\"mid\">");

		    for (String m : strings) {
			out.println("<option value=\"" + m + "\">" + m
				+ "</option>");
		    }
		    out.println("</select>");
		    out.println("<input name=\"Submit\" type=\"submit\"/>");
		    out.println("</form>");
		} else {
		    out.println("No missiles to intercept");
		}
	    else {
		out.println("No missiles to intercept");
	    }
	} else {
	    out.println("Trying to intercept " + mid);
	    w.interceptGivenMissile(mid);
	    // CHECK interception
	}
	out.println("\n<a href=\"/WarServlet\"><input type=\"button\" value=\"Back to main menu\"></a>");
	out.println("</html>");
    }

}
