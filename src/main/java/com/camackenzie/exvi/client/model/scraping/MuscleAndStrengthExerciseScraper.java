/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.camackenzie.exvi.client.model.scraping;

import com.camackenzie.exvi.core.async.Computation;
import com.camackenzie.exvi.core.async.ComputationFuture;
import com.camackenzie.exvi.core.async.FutureWrapper;
import com.camackenzie.exvi.core.model.*;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebClientOptions;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.util.ArrayList;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 *
 * @author callum
 */
public class MuscleAndStrengthExerciseScraper implements ExerciseWebScraper {

    public static void main(String[] args)
            throws IOException {
        ExerciseWebScraper scraper = new MuscleAndStrengthExerciseScraper();
        scraper.scrapeAndSaveToFileAsJson(args.length > 1 ? args[0] : "./exercises.json");
    }

    private static final String SITE = "https://www.muscleandstrength.com/";

    @Override
    public FutureWrapper<List<Exercise>> scrape() {
        return ComputationFuture.
                <List<Exercise>>startComputation(new ScrapeComputation())
                .wrapped();
    }

    private class ScrapeComputation implements Computation<List<Exercise>> {

        private List<Exercise> exercises = new CopyOnWriteArrayList<>();
        private Set<String> exerciseLinks = new CopyOnWriteArraySet<>();

        @Override
        public void run() {
            System.out.println("Beginning scraping process...");

            WebClient wc = new WebClient(BrowserVersion.CHROME);
            WebClientOptions wco = wc.getOptions();
            wco.setDownloadImages(false);
            wco.setCssEnabled(false);
            wco.setThrowExceptionOnScriptError(false);
            wco.setThrowExceptionOnFailingStatusCode(false);
            wco.setPrintContentOnFailingStatusCode(false);
            wco.setJavaScriptEnabled(false);
            wco.setWebSocketEnabled(false);

            try {

                HtmlPage htmlPage = wc.getPage(SITE + "/exercises");

                // Retrieve list of exercise page links and retreieve the data from each
                for (var elem : htmlPage.getByXPath("(//div[@class='mainpage-category-list exercise-category-list'])[1]/div[1]/*")) {
                    HtmlElement cellDiv = (HtmlElement) elem;
                    String href = ((HtmlAnchor) cellDiv.getFirstByXPath("./a")).getHrefAttribute();
                    System.out.println("Retrieving " + href);
                    this.getExercisesFromCategoryLink(wc, SITE + href);
                }

            } catch (InterruptedException e) {
                System.err.println(e);
                return;
            } catch (Exception ex) {
                System.err.println(ex);
            }

            wc.getCurrentWindow().getJobManager().removeAllJobs();
            wc.close();
        }

        private void getExercisesFromCategoryLink(WebClient wc, String link)
                throws IOException, InterruptedException {

            // Retrieve the page
            HtmlPage htmlPage = wc.getPage(link);
            String exerciseCellsXPath = "//div[@id='mnsview-list']"
                    + "/div[2]"
                    + "/div[1]"
                    + "/*";

            // Get the link to the last page from the buttons at the bottom
            int pageCount = 1;

            HtmlAnchor lastPageButtonAnchor = (HtmlAnchor) htmlPage
                    .getFirstByXPath("//div[@class='item-list']/ul[@class='pager']/li[last()]/a");

            if (lastPageButtonAnchor != null) {
                String lastPageLink = lastPageButtonAnchor
                        .getHrefAttribute();
                String lastPageLinkPrefix = "page=";
                // Determine the integer repersenting the index of the last page from the link
                pageCount = Integer.parseInt(lastPageLink
                        .substring(lastPageLink
                                .indexOf(lastPageLinkPrefix) + lastPageLinkPrefix.length())) + 1;
            }
            System.out.println("Page count: " + pageCount);

            // Iterate over all valid pages, retrieving all exercises from each
            int page = 0;
            while (page < pageCount) {
                System.out.println("Loading page " + page);
                this.getExercisesFromHtmlPageCategory(wc, htmlPage, exerciseCellsXPath);
                if (page + 1 < pageCount) {
                    htmlPage = wc.getPage(link + "&page=" + ++page);
                } else {
                    break;
                }
            }

        }

        private void getExercisesFromHtmlPageCategory(WebClient wc, HtmlPage htmlPage, String cellPath)
                throws IOException {
            // Iterate over all exercises on the given page
            for (var elem : htmlPage.getByXPath(cellPath)) {
                HtmlElement nodeElem = (HtmlElement) elem;
                HtmlAnchor anchorElem = nodeElem.getFirstByXPath("./div[@class='node-image']/a[1]");
                System.out.println("   No. " + this.exercises.size() + ": " + anchorElem.getHrefAttribute());
                if (this.exerciseLinks.contains(anchorElem.getHrefAttribute())) {
                    this.exerciseLinks.add(anchorElem.getHrefAttribute());
                }
                // Retireve exercise from link
                Exercise ex = this.getExerciseFromLink(wc, SITE + anchorElem.getHrefAttribute());
                this.exercises.add(ex);
            }
        }

        private Exercise getExerciseFromLink(WebClient wc, String link) throws IOException {
            // Request the webpage
            HtmlPage htmlPage = wc.getPage(link);

            // Retrieve the profile div
            HtmlElement exerciseProfileDiv = htmlPage.getFirstByXPath("//div[@class='node-stats-block']");

            // Scrape the exercise name
            String exerciseName = ((HtmlElement) htmlPage
                    .getFirstByXPath("//div[@class='content']/div[@class='node-header']/h1[1]"))
                    .getTextContent().trim().replace("Video Exercise Guide", "").trim();

            // Parse the exercise profile data
            HashMap<ExerciseProfile, String> exProfileInfo = new HashMap<>();
            for (var sectionObj : exerciseProfileDiv.getByXPath("./ul[1]/li")) {
                HtmlElement section = (HtmlElement) sectionObj;
                String label = ((HtmlElement) section.getFirstByXPath("./span[@class='row-label']"))
                        .getTextContent().trim();
                String content = section.getTextContent().replace(label, "").trim();
                exProfileInfo.put(ExerciseProfile.fromString(label), content);
            }

            // Refine exercise profile data
            String forceType = exProfileInfo.get(ExerciseProfile.FORCE_TYPE)
                    .toLowerCase()
                    .trim()
                    .replaceAll("([()]|unilateral|bilateral)+", "")
                    .replaceAll("n/a", "other")
                    .replaceAll("^press", "push")
                    .trim();
            exProfileInfo.put(ExerciseProfile.FORCE_TYPE, forceType);

            // Get the exercise instructions, tips, and overview div
            HtmlElement textDiv = htmlPage
                    .getFirstByXPath("//article[@class='node-body-content']"
                            + "/div[@class='content clearfix']");

            // Categorize different data headings on the page
            Scanner sc = new Scanner(textDiv.getTextContent());
            sc.useDelimiter("\\s*\n\\s*");
            HashMap<MainExerciseData, StringBuilder> pageContentMap = MainExerciseData.stringBuilderMap();
            String next = sc.next().trim();
            while (sc.hasNext()) {
                if (next.isBlank()) {
                    next = sc.next().trim();
                    continue;
                }
                boolean matchFound = false;
                for (var key : pageContentMap.keySet()) {
                    if (key.matchesTitle(exerciseName, next)) {
                        while (sc.hasNext()) {
                            next = sc.next().trim();
                            if (next.isBlank()) {
                                continue;
                            }
                            boolean currentInTitleSet = false;
                            for (var k : pageContentMap.keySet()) {
                                if (k.matchesTitle(exerciseName, next)) {
                                    currentInTitleSet = true;
                                    break;
                                }
                            }
                            if (currentInTitleSet) {
                                break;
                            }
                            matchFound = true;
                            pageContentMap.get(key).append(next).append("\n");
                        }
                        break;
                    }
                }
                if (!matchFound) {
                    next = "";
                }
            }

            // Retrieve video link
            HtmlElement videoElement, videoDiv = htmlPage.getFirstByXPath("//div[@class='video-wrap']");
            String videoLink = "";
            if ((videoElement = videoDiv.getFirstByXPath(".//iframe")) != null) {
                // Likely a youtube video
                videoLink = videoElement.getAttribute("src");
            } else if ((videoElement = videoDiv.getFirstByXPath(".//video")) != null) {
                // Embedded video
                HtmlElement videoSourceElement = (HtmlElement) videoElement.getFirstByXPath(".//source");
                videoLink = "https://" + videoSourceElement.getAttribute("src").replaceAll("\\/\\/", "");
            }

            // Parse exercise type
            HashSet<ExerciseType> exerciseTypes = new HashSet<>();
            for (var exerciseType : exProfileInfo.get(ExerciseProfile.EXERCISE_TYPE).toLowerCase().trim().split("\\s+,")) {
                switch (exerciseType.trim()) {
                    case "strength":
                        exerciseTypes.add(ExerciseType.STRENGTH);
                        break;
                    case "warmup":
                        exerciseTypes.add(ExerciseType.WARMUP);
                        break;
                    case "smr":
                        exerciseTypes.add(ExerciseType.WARMUP);
                        exerciseTypes.add(ExerciseType.COOLDOWN);
                        break;
                    case "plyometrics":
                        exerciseTypes.add(ExerciseType.PLYOMETRIC);
                        break;
                    case "activation":
                        exerciseTypes.add(ExerciseType.WARMUP);
                        break;
                    case "conditioning":
                        exerciseTypes.add(ExerciseType.CONDITIONING);
                        break;
                    case "olympic weightlifting":
                        exerciseTypes.add(ExerciseType.STRENGTH);
                        exerciseTypes.add(ExerciseType.POWER_LIFTING);
                        break;
                    case "powerlifting":
                        exerciseTypes.add(ExerciseType.STRENGTH);
                        exerciseTypes.add(ExerciseType.POWER_LIFTING);
                        break;
                    case "strongman":
                        exerciseTypes.add(ExerciseType.STRENGTH);
                        exerciseTypes.add(ExerciseType.POWER_LIFTING);
                        break;
                    case "stretching":
                        exerciseTypes.add(ExerciseType.WARMUP);
                        exerciseTypes.add(ExerciseType.COOLDOWN);
                        break;
                    default:
                        System.err.println("Unknown exercise type: " + exerciseType);
                        break;
                }
            }

            // Parse primary muscle data
            ArrayList<MuscleWorkData> muscleData = new ArrayList<>();
            for (var muscleStr : exProfileInfo.get(ExerciseProfile.TARGET_MUSCLE_GROUP).split("\\s+,")) {
                muscleStr = muscleStr.trim();
                for (var muscle : Muscle.class.getEnumConstants()) {
                    if (muscle.matchesName(muscleStr)) {
                        muscleData.add(new MuscleWorkData(muscle, 1));
                        break;
                    }
                }
            }

            // Parse secondary muscle data
            for (var muscleStr : exProfileInfo.get(ExerciseProfile.SECONDARY_MUSCLES).split("\\s+,")) {
                muscleStr = muscleStr.trim();
                for (var muscle : Muscle.class.getEnumConstants()) {
                    if (muscle.matchesName(muscleStr)) {
                        muscleData.add(new MuscleWorkData(muscle, 2));
                        break;
                    }
                }
            }

            // Parse equipment data
            HashSet<ExerciseEquipment> equipment = new HashSet<>();
            equipment.add(new ExerciseEquipment(
                    exProfileInfo.get(ExerciseProfile.EQUIPMENT)));

            return new Exercise(exerciseName,
                    pageContentMap.get(MainExerciseData.INSTRUCTIONS).toString(),
                    videoLink,
                    pageContentMap.get(MainExerciseData.TIPS).toString(),
                    pageContentMap.get(MainExerciseData.OVERVIEW).toString(),
                    muscleData.toArray(sz -> new MuscleWorkData[sz]),
                    exerciseTypes,
                    ExerciseExperienceLevel.fromString(exProfileInfo.get(ExerciseProfile.EXPERIENCE)),
                    ExerciseMechanics.fromString(exProfileInfo.get(ExerciseProfile.MECHANICS)),
                    ExerciseForceType.fromString(forceType),
                    equipment
            );
        }

        @Override
        public List<Exercise> getResult() {
            return this.exercises;
        }

    }

    private enum ExerciseProfile {
        TARGET_MUSCLE_GROUP("Target Muscle Group"), // Covered
        EXERCISE_TYPE("Exercise Type"), // Covered
        EQUIPMENT("Equipment Required"), // Covered
        MECHANICS("Mechanics"), // Covered
        FORCE_TYPE("Force Type"), // Covered
        EXPERIENCE("Experience Level"), // Covered
        SECONDARY_MUSCLES("Secondary Muscles"); // Covered

        private final String title;

        private ExerciseProfile(String title) {
            this.title = title;
        }

        public String getSiteTitle() {
            return this.title;
        }

        public static ExerciseProfile fromString(String str) {
            for (var profile : ExerciseProfile.class.getEnumConstants()) {
                if (profile.getSiteTitle().equalsIgnoreCase(str)) {
                    return profile;
                }
            }
            throw new RuntimeException("No exercise profile match found.");
        }
    }

    private enum MainExerciseData {
        INSTRUCTIONS("Instructions", "Instructions"),
        OVERVIEW("Overview", "Overview"),
        TIPS("Tips", "Tips:", "Exercise Tips");

        private final String siteName;
        private final String[] otherNames;
        private final String contains;

        private MainExerciseData(String sn, String contains, String... otherNames) {
            this.siteName = sn;
            this.otherNames = otherNames;
            this.contains = contains;
        }

        public String getSiteName(String exerciseName) {
            return exerciseName + this.siteName;
        }

        public boolean matchesTitle(String exerciseName, String in) {
            if (in.endsWith(this.contains)) {
                return true;
            }
            in = in.replace(":", "").trim();
            if (this.getSiteName(exerciseName).equals(in) || this.siteName.equals(in)) {
                return true;
            }
            for (var st : this.otherNames) {
                if (st.equals(in)) {
                    return true;
                }
            }
            return false;
        }

        public static HashMap<MainExerciseData, StringBuilder> stringBuilderMap() {
            HashMap<MainExerciseData, StringBuilder> ret = new HashMap<>();
            for (var i : MainExerciseData.class.getEnumConstants()) {
                ret.put(i, new StringBuilder());
            }
            return ret;
        }
    }

}
