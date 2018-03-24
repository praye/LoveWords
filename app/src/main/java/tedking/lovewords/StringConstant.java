package tedking.lovewords;


/**
 * Created by Administrator on 2018/3/23.
 * Used in sharedPreference
 */

public class StringConstant {
    public static final String SONGID = "songId";  // Identify which song should play when alarm rings, return int;
    public static final String QUESTIONNUMBER = "question_number"; // The number of question, return int;
    public static final String STARTFRAGMENTID = "startFragmentId"; // The Fragment which to show when app run, return int;
    public static final String SHAREDPREFERENCENAME = "sharedPreferences"; // The name of sharedPreference;
    public static final String FIRSTOPENAPP = "firstOpenApp"; // Whether the app is first open, return boolean;
    public static final String WORDSTOSENDTOCLOUD = "words to send to cloud"; // The words that should send to cloud, return String;
    public static final String ALLWORDSHAVEMASTERED = "all words have been mastered"; // Whether all words have been mastered, return boolean;
}
