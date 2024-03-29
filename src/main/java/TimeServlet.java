import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/time")
public class TimeServlet extends HttpServlet {
    private TemplateEngine engine;

    @Override
    public void init() {
        engine = new TemplateEngine();
        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix("./templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");

        Map<String, String[]> parameterMap = req.getParameterMap();

        Map<String, Object> params = new LinkedHashMap<>();
        for (Map.Entry<String, String[]> keyValue : parameterMap.entrySet()) {
            params.put(keyValue.getKey(), keyValue.getValue()[0]);
        }

        Context simpleContext = new Context(
                req.getLocale(),
                Map.of("queryParams", params)
        );

        engine.process("test", simpleContext, resp.getWriter());
        resp.getWriter().close();
    }

    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html; charset=utf-8");
        String timezone = req.getParameter("timezone");
        resp.getWriter().write(getTimeAndUTC(timezone));
        resp.getWriter().close();
    }

    public String getTimeAndUTC(String timezone) {
        DateTime dateTime = new DateTime().withZone(DateTimeZone.forID("GMT"));
        dateTime = getDateWithUtc(getUTCDigit(timezone), dateTime);
        return dateTime.toString("yyyy-MM-dd HH:mm:ss") + " " + timezone;
    }

    private int getUTCDigit(String utcParameter) {
        if (utcParameter.contains("-")) {
            return Integer.parseInt(utcParameter.substring((utcParameter.indexOf("-"))));
        } if (utcParameter.contains("+")) {
            return Integer.parseInt(utcParameter.substring((utcParameter.indexOf(utcParameter.substring(4)))));
        }
        else return 0;
    }

    private DateTime getDateWithUtc(int utc, DateTime dateTime) {
            return dateTime.plusHours(utc);
    }
}