import java.util.*;

public class SocialMediaUsernameAvailabilityChecker {

    HashMap<String, Integer> users = new HashMap<>();
    HashMap<String, Integer> attempts = new HashMap<>();

    public SocialMediaUsernameAvailabilityChecker () {
        users.put("john_doe", 101);
        users.put("alex", 102);
        users.put("admin", 1);
    }

    // Check availability
    public boolean checkAvailability(String username) {

        attempts.put(username, attempts.getOrDefault(username, 0) + 1);

        return !users.containsKey(username);
    }

    // Suggest alternatives
    public List<String> suggestAlternatives(String username) {

        List<String> suggestions = new ArrayList<>();

        for(int i=1;i<=3;i++){
            String suggestion = username + i;

            if(!users.containsKey(suggestion)){
                suggestions.add(suggestion);
            }
        }

        suggestions.add(username.replace("_","."));

        return suggestions;
    }

    // Get most attempted username
    public String getMostAttempted() {

        String result = "";
        int max = 0;

        for(Map.Entry<String,Integer> entry : attempts.entrySet()){

            if(entry.getValue() > max){
                max = entry.getValue();
                result = entry.getKey();
            }
        }

        return result + " (" + max + " attempts)";
    }

    public static void main(String[] args) {

        SocialMediaUsernameAvailabilityChecker  checker = new SocialMediaUsernameAvailabilityChecker ();

        System.out.println(checker.checkAvailability("john_doe"));
        System.out.println(checker.checkAvailability("jane_smith"));

        System.out.println(checker.suggestAlternatives("john_doe"));

        System.out.println(checker.getMostAttempted());
    }
}


