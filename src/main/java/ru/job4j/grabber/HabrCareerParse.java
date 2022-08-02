package ru.job4j.grabber;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class HabrCareerParse {
    private static final String SOURCE_LINK = "https://career.habr.com";
    private static final String PAGE_LINK = String.format("%s/vacancies/java_developer?page=", SOURCE_LINK);

    public static void main(String[] args) throws IOException {
        int index = 1;
        while (index <= 5) {
            Connection connection = Jsoup.connect(PAGE_LINK + index);
            Document document = connection.get();
            Elements rows = document.select(".vacancy-card__inner");
            rows.forEach(row -> {
                Element titleElement = row.select(".vacancy-card__title").first();
                Element linkElement = titleElement.child(0);
                String vacancyName = titleElement.text();
                Element dateElement = row.select(".vacancy-card__date").first();
                Element dateLinkElement = dateElement.child(0);
                LocalDate date = LocalDate.parse(dateLinkElement.attr("datetime"), DateTimeFormatter.ISO_DATE_TIME);
                String link = String.format("%s%s", SOURCE_LINK, linkElement.attr("href"));
                Element vacancyElement = row.select(".vacancy-card__icon-link").first();
                System.out.printf("%s %s %s%n", vacancyName, link, date);
                String linkVacancy = vacancyElement.attr("href");
                try {
                   String description = retrieveDescription(linkVacancy);
                    System.out.printf(" Описание вакансии%n %s%n", description);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            index++;
        }
    }

    private static String retrieveDescription(String link) throws IOException {
        Connection connection = Jsoup.connect(String.format("%s/" + link, SOURCE_LINK));
        Document document = connection.get();
        Elements element =   document.select(".style-ugc");
        return element.text();
    }
}

