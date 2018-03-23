package tedking.lovewords;

/**
 * Created by Administrator on 2018/3/23.
 */

public class WordMeaning {
    private String worditself;
    private String meaning;
    public WordMeaning(String worditself, String meaning){
        this.worditself = worditself;
        this.meaning = meaning;
    }
    public String getWorditself(){
        return worditself;
    }
    public String getMeaning(){
        return meaning;
    }
}
