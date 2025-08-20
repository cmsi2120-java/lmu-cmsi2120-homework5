package wiki.solution;

import java.util.*;

public class WikiWalker {
    
    private HashMap<String, TreeMap<String, Integer>> siteMap;
    
    WikiWalker () {
        siteMap = new HashMap<>();
    }
    
    /**
     * Adds an article with the given name to the site map and associates the
     * given linked articles found on the page. Duplicate links in that list are
     * ignored, as should an article's links to itself.
     * @param articleName The name of the page's article
     * @param articleLinks List of names for those articles linked on the page
     */
    public void addArticle (String articleName, List<String> articleLinks) {
        TreeMap<String, Integer> results = new TreeMap<>();
        for (String a : articleLinks) {
            results.put(a, 0);
        }
        siteMap.put(articleName, results);
    }
    
    /**
     * Determines whether or not, based on the added articles with their links,
     * there is *some* sequence of links that could be followed to take
     * the user from the source article to the destination.
     * @param src The beginning article of the possible path
     * @param dest The end article along a possible path
     * @return boolean representing whether or not that path exists
     */
    public boolean hasPath (String src, String dest) {
        HashSet<String> visited = new HashSet<>();
        return hasPath(src, dest, visited);
    }
    
    private boolean hasPath (String src, String dest, Set<String> visited) {
        if (src.equals(dest)) {
            return true;
        }
        if (!siteMap.containsKey(src)) {
            return false;
        }
        
        for (String a : siteMap.get(src).keySet()) {
            String newDest = a;
            if (!visited.contains(newDest)) {
                visited.add(newDest);
                if (hasPath(newDest, dest, visited)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /**
     * Increments the click counts of each link along some trajectory. For
     * instance, a trajectory of ["A", "B", "C"] will increment the click
     * count of the "B" link on the "A" page, and the count of the "C" link
     * on the "B" page. Assume that all given trajectories are valid,
     * meaning that a link exists from page i to i+1 for each index i
     * @param traj A sequence of a user's page clicks; must be at least
     * 2 article names in length
     */
    public void logTrajectory (List<String> traj) {
        String current = traj.get(0);
        TreeMap<String, Integer> currLinks = siteMap.get(current);
        for (int i = 1; i < traj.size(); i++) {
            current = traj.get(i);
            currLinks.put(current, currLinks.get(current) + 1);
            currLinks = (i == traj.size()-1) ? null : siteMap.get(current);
        }
    }
    
    /**
     * Returns the number of clickthroughs recorded from the src article
     * to the destination article. If the destination article is not
     * a link directly reachable from the src, returns -1.
     * @param src The article on which the clickthrough occurs.
     * @param dest The article requested by the clickthrough.
     * @throws IllegalArgumentException if src isn't in site map
     * @return The number of times the destination has been requested
     * from the source.
     */
    public int clickthroughs (String src, String dest) {
        if (!siteMap.containsKey(src)) {
            throw new IllegalArgumentException();
        }
        if (!siteMap.get(src).containsKey(dest)) {
            return -1;
        }
        TreeMap<String, Integer> currMap = siteMap.get(src);
        return (currMap.keySet().contains(dest)) ? currMap.get(dest) : -1;
    }
    
    /**
     * Based on the pattern of clickthrough trajectories recorded by this
     * WikiWalker, returns the most likely trajectory of k clickthroughs
     * starting at (but not including in the output) the given src article.
     * Duplicates and cycles are valid output along a most likely trajectory.
     * In the event of a tie in max clickthrough "weight," this method will choose
     * the link earliest in the ascending alphabetic order of those tied.
     * @param src The starting article of the trajectory (which will not be
     * included in the output)
     * @param k The maximum length of the desired trajectory (though may be
     * shorter in the case that the trajectory ends with a terminal article).
     * @return A List containing the ordered article names of the most likely
     * trajectory starting at src.
     */
    public List<String> mostLikelyTrajectory (String src, int k) {
        if (!siteMap.containsKey(src)) {
            throw new IllegalArgumentException();
        }
        LinkedList<String> result = new LinkedList<>();
        TreeMap<String, Integer> currArt = siteMap.get(src);
        for (; k > 0; k--) {
            String currBest = null;
            int currGreatest = -1;
            for(Map.Entry<String,Integer> entry : currArt.entrySet()) {
                if (entry.getValue() > currGreatest) {
                    currBest = entry.getKey();
                    currGreatest = entry.getValue();
                }
            }
            result.add(currBest);
            if (!siteMap.containsKey(currBest)) {
                return result;
            }
            currArt = siteMap.get(currBest);
        }
        return result;
    }
    
}
