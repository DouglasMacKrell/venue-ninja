package com.venueninja.testdata;

import com.venueninja.model.Venue;
import com.venueninja.model.SeatRecommendation;

import java.util.Arrays;
import java.util.List;

/**
 * Test data builder utility for creating consistent test data across all tests.
 * This ensures all tests use the same data structure and reduces duplication.
 */
public class TestDataBuilder {

    /**
     * Creates Madison Square Garden venue with seat recommendations
     */
    public static Venue createMadisonSquareGarden() {
        SeatRecommendation lowerBowl = new SeatRecommendation();
        lowerBowl.setSection("104");
        lowerBowl.setCategory("Lower Bowl");
        lowerBowl.setReason("Best resale value & view of stage");
        lowerBowl.setEstimatedPrice("$250");
        lowerBowl.setTip("Avoid row 20+ due to rigging obstruction");

        SeatRecommendation upperBowl = new SeatRecommendation();
        upperBowl.setSection("200");
        upperBowl.setCategory("Upper Bowl");
        upperBowl.setReason("Great value for price-conscious fans");
        upperBowl.setEstimatedPrice("$75");
        upperBowl.setTip("Bring binoculars for optimal viewing");

        SeatRecommendation bridge = new SeatRecommendation();
        bridge.setSection("Bridge");
        bridge.setCategory("Premium");
        bridge.setReason("Unique perspective and premium experience");
        bridge.setEstimatedPrice("$400");
        bridge.setTip("Limited availability, book early");

        Venue msg = new Venue();
        msg.setId("msg");
        msg.setName("Madison Square Garden");
        msg.setRecommendations(Arrays.asList(lowerBowl, upperBowl, bridge));

        return msg;
    }

    /**
     * Creates Yankee Stadium venue with seat recommendations
     */
    public static Venue createYankeeStadium() {
        SeatRecommendation fieldLevel = new SeatRecommendation();
        fieldLevel.setSection("Field Level");
        fieldLevel.setCategory("Premium");
        fieldLevel.setReason("Close to the action and players");
        fieldLevel.setEstimatedPrice("$300");
        fieldLevel.setTip("Sections 1-20 offer best views");

        SeatRecommendation mainLevel = new SeatRecommendation();
        mainLevel.setSection("Main Level");
        mainLevel.setCategory("Standard");
        mainLevel.setReason("Good balance of view and price");
        mainLevel.setEstimatedPrice("$150");
        mainLevel.setTip("Avoid sections behind home plate for concerts");

        SeatRecommendation terrace = new SeatRecommendation();
        terrace.setSection("Terrace");
        terrace.setCategory("Budget");
        terrace.setReason("Affordable option with decent view");
        terrace.setEstimatedPrice("$80");
        terrace.setTip("Higher rows have better sight lines");

        Venue yankee = new Venue();
        yankee.setId("yankee");
        yankee.setName("Yankee Stadium");
        yankee.setRecommendations(Arrays.asList(fieldLevel, mainLevel, terrace));

        return yankee;
    }

    /**
     * Creates Barclays Center venue with seat recommendations
     */
    public static Venue createBarclaysCenter() {
        SeatRecommendation lowerLevel = new SeatRecommendation();
        lowerLevel.setSection("Lower Level");
        lowerLevel.setCategory("Premium");
        lowerLevel.setReason("Excellent sight lines and premium experience");
        lowerLevel.setEstimatedPrice("$200");
        lowerLevel.setTip("Sections 1-15 are closest to stage");

        SeatRecommendation upperLevel = new SeatRecommendation();
        upperLevel.setSection("Upper Level");
        upperLevel.setCategory("Value");
        upperLevel.setReason("Good value for the price");
        upperLevel.setEstimatedPrice("$60");
        upperLevel.setTip("Avoid extreme side sections");

        Venue barclays = new Venue();
        barclays.setId("barclays");
        barclays.setName("Barclays Center");
        barclays.setRecommendations(Arrays.asList(lowerLevel, upperLevel));

        return barclays;
    }

    /**
     * Creates a venue with no recommendations
     */
    public static Venue createVenueWithNoRecommendations() {
        Venue emptyVenue = new Venue();
        emptyVenue.setId("empty");
        emptyVenue.setName("Empty Venue");
        emptyVenue.setRecommendations(Arrays.asList());
        return emptyVenue;
    }

    /**
     * Creates a venue with null recommendations
     */
    public static Venue createVenueWithNullRecommendations() {
        Venue nullVenue = new Venue();
        nullVenue.setId("null");
        nullVenue.setName("Null Recommendations Venue");
        nullVenue.setRecommendations(null);
        return nullVenue;
    }

    /**
     * Creates a venue with special characters in name
     */
    public static Venue createVenueWithSpecialCharacters() {
        Venue specialVenue = new Venue();
        specialVenue.setId("special");
        specialVenue.setName("Madison Square Garden & Arena (MSG) - The World's Most Famous Arena!");
        specialVenue.setRecommendations(Arrays.asList());
        return specialVenue;
    }

    /**
     * Creates a venue with a very long name
     */
    public static Venue createVenueWithLongName() {
        Venue longNameVenue = new Venue();
        longNameVenue.setId("long");
        longNameVenue.setName("This is a very long venue name that might exceed normal expectations for venue naming conventions and could potentially cause issues with display or storage");
        longNameVenue.setRecommendations(Arrays.asList());
        return longNameVenue;
    }

    /**
     * Creates a venue with numeric ID
     */
    public static Venue createVenueWithNumericId() {
        Venue numericVenue = new Venue();
        numericVenue.setId("123");
        numericVenue.setName("Numeric ID Venue");
        numericVenue.setRecommendations(Arrays.asList());
        return numericVenue;
    }

    /**
     * Creates a venue with mixed case ID
     */
    public static Venue createVenueWithMixedCaseId() {
        Venue mixedCaseVenue = new Venue();
        mixedCaseVenue.setId("MsG");
        mixedCaseVenue.setName("Mixed Case ID Venue");
        mixedCaseVenue.setRecommendations(Arrays.asList());
        return mixedCaseVenue;
    }

    /**
     * Creates a venue with hyphenated ID
     */
    public static Venue createVenueWithHyphenatedId() {
        Venue hyphenatedVenue = new Venue();
        hyphenatedVenue.setId("msg-arena");
        hyphenatedVenue.setName("Hyphenated ID Venue");
        hyphenatedVenue.setRecommendations(Arrays.asList());
        return hyphenatedVenue;
    }

    /**
     * Creates a venue with underscore ID
     */
    public static Venue createVenueWithUnderscoreId() {
        Venue underscoreVenue = new Venue();
        underscoreVenue.setId("msg_arena");
        underscoreVenue.setName("Underscore ID Venue");
        underscoreVenue.setRecommendations(Arrays.asList());
        return underscoreVenue;
    }

    /**
     * Creates all standard test venues
     */
    public static List<Venue> createAllTestVenues() {
        return Arrays.asList(
            createMadisonSquareGarden(),
            createYankeeStadium(),
            createBarclaysCenter()
        );
    }

    /**
     * Creates edge case test venues
     */
    public static List<Venue> createEdgeCaseVenues() {
        return Arrays.asList(
            createVenueWithNoRecommendations(),
            createVenueWithNullRecommendations(),
            createVenueWithSpecialCharacters(),
            createVenueWithLongName(),
            createVenueWithNumericId(),
            createVenueWithMixedCaseId(),
            createVenueWithHyphenatedId(),
            createVenueWithUnderscoreId()
        );
    }

    /**
     * Creates a single seat recommendation
     */
    public static SeatRecommendation createSampleSeatRecommendation() {
        SeatRecommendation recommendation = new SeatRecommendation();
        recommendation.setSection("Sample Section");
        recommendation.setCategory("Sample Category");
        recommendation.setReason("Sample reason for recommendation");
        recommendation.setEstimatedPrice("$100");
        recommendation.setTip("Sample tip for this seat");
        return recommendation;
    }

    /**
     * Creates a seat recommendation with special characters
     */
    public static SeatRecommendation createSeatRecommendationWithSpecialCharacters() {
        SeatRecommendation recommendation = new SeatRecommendation();
        recommendation.setSection("Section 104 & 105");
        recommendation.setCategory("Premium & VIP");
        recommendation.setReason("Best view & experience!");
        recommendation.setEstimatedPrice("$250-$300");
        recommendation.setTip("Avoid rows 20+ due to rigging obstruction & limited visibility");
        return recommendation;
    }

    /**
     * Creates a seat recommendation with very long text
     */
    public static SeatRecommendation createSeatRecommendationWithLongText() {
        SeatRecommendation recommendation = new SeatRecommendation();
        recommendation.setSection("This is a very long section name that might exceed normal expectations for section naming conventions");
        recommendation.setCategory("This is a very long category name that might exceed normal expectations for category naming conventions");
        recommendation.setReason("This is a very long reason that might exceed normal expectations for reason text length and could potentially cause issues with display or storage");
        recommendation.setEstimatedPrice("$100-$200");
        recommendation.setTip("This is a very long tip that might exceed normal expectations for tip text length and could potentially cause issues with display or storage");
        return recommendation;
    }
} 