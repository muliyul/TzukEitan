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
 * Servlet implementation class InterceptLauncher
 */
@WebServlet("/InterceptLauncher")
public class InterceptLauncher extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public InterceptLauncher() {
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
	String lid = request.getParameter("lid");
	out.println("<html>");
	Vector<String> strings = w.getAllVisibleLaunchersIds();
	if (lid == null) {
	    if (strings != null)
		if (strings.size() > 0) {
		    out.println("<form name=\"interceptLauncherForm\" action=\"/WarServlet/InterceptLauncher\" method=\"POST\">");
		    out.println("Launchers: ");
		    out.println("<select name=\"lid\">");

		    for (String m : strings) {
			out.println("<option value=\"" + m + "\">" + m
				+ "</option>");
		    }
		    out.println("</select>");
		    out.println("<input name=\"Submit\" type=\"submit\"/>");
		    out.println("</form>");
		} else {
		    out.println("No launcher to intercept");
		}
	    else {
		out.println("No launcher to intercept");
	    }
	} else {
	    out.println("Trying to intercept " + lid);
	    w.interceptGivenLauncher(lid);
	    // CHECK interception
	}
	out.println("\n<a href=\"/WarServlet\"><input type=\"button\" value=\"Back to main menu\"></a>");
	out.println("</html>");
    }

}
