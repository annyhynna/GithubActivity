package ecs189.querying.github;

import ecs189.querying.Util;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Vincent on 10/1/2017.
 */
public class GithubQuerier {

    private static final String BASE_URL = "https://api.github.com/users/";

    public static String eventsAsHTML(String user) throws IOException, ParseException {
        List<JSONObject> response = getEvents(user);
        StringBuilder sb = new StringBuilder();
        sb.append("<div>");
        for (int i = 0; i < response.size(); i++) {
            JSONObject event = response.get(i);
            // Get event type
            String type = event.getString("type");
            // Get created_at date, and format it in a more pleasant style
            String creationDate = event.getString("created_at");
            SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
            SimpleDateFormat outFormat = new SimpleDateFormat("dd MMM, yyyy");
            Date date = inFormat.parse(creationDate);
            String formatted = outFormat.format(date);

            // Get repo name
            String repo = event.getJSONObject("repo").getString("name");
            String repoURL = "https://github.com/" + repo;

            List<String> shas = new ArrayList<String>();
            List<String> messages = new ArrayList<String>();

            // Get commits info, which contains SHA and message
            JSONArray commits = event.getJSONObject("payload").getJSONArray("commits");

            sb.append("<div class=\"row\">");
            sb.append("<div class=\"col-md-8\">");
            // Add type of event as header
            sb.append("<h3 class=\"type\">");
            sb.append(type);
            sb.append("</h3>");
            // Add formatted date
            sb.append(" on ");
            sb.append(formatted);
            sb.append("<br />");
            // Add collapsible JSON textbox (don't worry about this for the homework; it's just a nice CSS thing I like)
            sb.append("<a data-toggle=\"collapse\" href=\"#event-" + i + "\">JSON</a>");
            sb.append("<div id=event-" + i + " class=\"collapse\" style=\"height: auto;\"> <pre>");
            sb.append(event.toString());
            sb.append("</pre> </div>");
            sb.append("</div>");
            sb.append("</div>");
            // Add table for SHA and message

            sb.append("<div class=\"container col-md-8\">");
            sb.append("<h4>Repository: <a href='" + repoURL + "' target='blank'>" + repo + "</a></h4>");
            sb.append("<h4>Commits</h4>");
            sb.append("<p>Click on the row to see detailed commit information</p>");
            sb.append("<table class=\"table table-hover\">");
            sb.append("<thead>");
            sb.append("<tr>");
            sb.append("<th>SHA</th>");
            sb.append("<th>Message</th>");
            sb.append("</tr>");
            sb.append("</thead>");
            sb.append("<tbody>");
            // Add sha and message
            for (int j = 0; j < commits.length(); j++) {
                JSONObject commit = commits.getJSONObject(j);
                String sha = commit.getString("sha");
                String message = commit.getString("message");
                String commitURL = repoURL + "/commit/" + sha;

                sb.append("<tr onclick=\"window.open('" + commitURL + "', '_blank');\">");
                sb.append("<td>" + sha.substring(0, 8) + "</td>");
                sb.append("<td>" + message + "</td>");
                sb.append("</tr>");
            }
            sb.append("</tbody>");
            sb.append("</table>");
            sb.append("</div>");
        }
        sb.append("</div>"); // row
        return sb.toString();
    }

    private static List<JSONObject> getEvents(String user) throws IOException {
        List<JSONObject> eventList = new ArrayList<JSONObject>();
        String url = BASE_URL + user + "/events";
        System.out.println(url);
        JSONObject json = Util.queryAPI(new URL(url));
//        System.out.println(json);
        JSONArray events = json.getJSONArray("root");
        int count = 0;
        for (int i = 0; i < events.length(); i++) {
            if (events.getJSONObject(i).get("type").equals("PushEvent")){
//                JSONObject commit = events.getJSONObject(i).getJSONObject("payload").getJSONArray("commits").getJSONObject(0);
//                System.out.println("SHA: " + commit.getString("sha"));
//                System.out.println("MSG: " + commit.getString("message"));
                eventList.add(events.getJSONObject(i));
                count++;
            }
            if (count == 10)
                break;
        }
        return eventList;
    }
}