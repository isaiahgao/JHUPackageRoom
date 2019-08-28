package work.packageroom;

public class PackageData {
    
    public PackageData(String packageId, String date, String from, boolean isOld) {
        this.date = date;
        this.from = from;
        this.pid = packageId;
        this.isOld = isOld;
    }
    
    public String pid, date, from;
    public boolean isOld;
}
