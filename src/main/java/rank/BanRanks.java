package rank;

import java.util.*;

public class BanRanks {
    private Map<String, Integer> ranks = new LinkedHashMap<>();

    public void ping(String uuid){
        int times = ranks.getOrDefault(uuid,0);
        times ++;
        ranks.put(uuid,times);
    }

    public Map<String, Integer> getRanks(){
        return sortByValueDescending(ranks);
    }

    public Map<String, String> getUuidToNameMappings(List<String> uuid){
        Map<String, String> mapping = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            String current = uuid.get(i);
            mapping.put(current,MojangAPI.getName(current));
        }
        return mapping;
    }



    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValueDescending(Map<K, V> map)
    {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());
        list.sort((o1, o2) -> {
            int compare = (o1.getValue()).compareTo(o2.getValue());
            return -compare;
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

}
