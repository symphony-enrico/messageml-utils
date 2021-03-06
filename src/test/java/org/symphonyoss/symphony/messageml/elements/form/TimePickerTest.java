package org.symphonyoss.symphony.messageml.elements.form;

import org.junit.Before;
import org.junit.Test;
import org.symphonyoss.symphony.messageml.elements.*;
import org.symphonyoss.symphony.messageml.exceptions.InvalidInputException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TimePickerTest extends ElementTest {

  private String formId;

  @Before
  public void beforeEach() {
    this.formId = "timepicker-form";
  }

  @Test
  public void testTimePickerBasic() throws Exception {
    String input = "<messageML><form id=\"" + formId + "\">" +
            "<time-picker " +
            "name=\"time-meeting\" " +
            "value=\"14:00:00\" " +
            "format=\"HH:mm:ss\" " +
            "strict=\"true\" " +
            "placeholder=\"Please pick a time\" " +
            "min=\"08:00:00\" " +
            "max=\"18:00:00\" " +
            "step=\"1800\" " +
            "disabled-time='[{\"time\": \"16:00:00\"}]' " +
            "required=\"true\"/>" +
            ACTION_BTN_ELEMENT + "</form></messageML>";

    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
    Element messageML = context.getMessageML();
    Element form = messageML.getChildren().get(0);
    Element timePicker = form.getChildren().get(0);

    String presentationML = context.getPresentationML();

    String EXPECTED_MARKDOWN = "Form (log into desktop client to answer):\n"
            + "---\n"
            + "(Time Picker:[Please pick a time])(Button:Send)\n"
            + "---\n";
    String expectedPresentationML = "<div data-format=\"PresentationML\" data-version=\"2.0\">" +
            "<form id=\"" + formId + "\">" +
            "<input type=\"time\" " +
            "name=\"time-meeting\" " +
            "value=\"14:00:00\" " +
            "placeholder=\"Please pick a time\" " +
            "min=\"08:00:00\" " +
            "max=\"18:00:00\" " +
            "step=\"1800\" " +
            "data-format=\"HH:mm:ss\" " +
            "data-strict=\"true\" " +
            "data-disabled-time='[{\"type\":\"time\",\"time\":\"16:00:00\"}]' " +
            "required=\"true\"/>" + ACTION_BTN_ELEMENT +
            "</form></div>";

    assertEquals(Form.class, form.getClass());
    assertEquals(TimePicker.class, timePicker.getClass());
    assertTrue("Text should be empty", timePicker.getChildren().isEmpty());
    assertEquals("Markdown", EXPECTED_MARKDOWN, context.getMarkdown());
    assertEquals("PresentationML", expectedPresentationML, presentationML);
  }

  @Test
  public void testTimePickerWithLabelAndTitle() throws Exception {
    String input = "<messageML>" +
            "<form id=\"" + formId + "\">" +
            "<time-picker " +
            "name=\"time-meeting\" " +
            "title=\"This is a hint\" " +
            "label=\"Meeting time\" " +
            "value=\"14:00:00\" " +
            "format=\"hh:mm\" " +
            "strict=\"true\" " +
            "placeholder=\"Please pick a time\" " +
            "min=\"08:00:00\" " +
            "max=\"18:00:00\" " +
            "step=\"1800\" " +
            "/>" +
            ACTION_BTN_ELEMENT + "</form></messageML>";

    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
    Element messageML = context.getMessageML();
    Element form = messageML.getChildren().get(0);
    Element timePicker = form.getChildren().get(0);

    String presentationML = context.getPresentationML();
    String timePickerRegex = ".*(\"time-picker-(.*?)\").*";
    Pattern pattern = Pattern.compile(timePickerRegex);
    Matcher matcher = pattern.matcher(presentationML);
    String uniqueLabelId = matcher.matches() ? matcher.group(2) : null;


    String EXPECTED_MARKDOWN = "Form (log into desktop client to answer):\n"
            + "---\n"
            + "(Time Picker:[Please pick a time][Meeting time][This is a hint])(Button:Send)\n"
            + "---\n";
    String expectedPresentationML = String.format("<div data-format=\"PresentationML\" data-version=\"2.0\">" +
            "<form id=\"" + formId + "\">" +
            "<div class=\"time-picker-group\" data-generated=\"true\">" +
            "<label for=\"time-picker-%s\">Meeting time</label>" +
            "<span class=\"info-hint\" data-target-id=\"time-picker-%s\" data-title=\"This is a hint\"></span>" +
            "<input type=\"time\" " +
            "name=\"time-meeting\" " +
            "value=\"14:00:00\" " +
            "placeholder=\"Please pick a time\" " +
            "min=\"08:00:00\" " +
            "max=\"18:00:00\" " +
            "step=\"1800\" " +
            "data-format=\"hh:mm\" " +
            "data-strict=\"true\" " +
            "id=\"time-picker-%s\"/>" +
            "</div>%s</form></div>", uniqueLabelId, uniqueLabelId, uniqueLabelId, ACTION_BTN_ELEMENT);

    assertEquals(Form.class, form.getClass());
    assertEquals(TimePicker.class, timePicker.getClass());
    assertTrue("Text should be empty", timePicker.getChildren().isEmpty());
    assertEquals("Markdown", EXPECTED_MARKDOWN, context.getMarkdown());
    assertEquals("PresentationML", expectedPresentationML, presentationML);
  }

  @Test
  public void testTimePickerDisabledTimeWithRange() throws Exception {
    String input = "<messageML>" +
            "<form id=\"form\">" +
            "<time-picker name=\"meeting-time\" " +
            "disabled-time='[{\"from\": \"12:00:00\", \"to\": \"14:00:00\"}, {\"time\": \"16:00:00\"}]'/>" +
            ACTION_BTN_ELEMENT + "</form></messageML>";
    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
    String expectedPresentationML = "<div data-format=\"PresentationML\" data-version=\"2.0\">" +
            "<form id=\"form\">" +
            "<input type=\"time\" " +
            "name=\"meeting-time\" " +
            "data-disabled-time='[{\"type\":\"range\",\"from\":\"12:00:00\",\"to\":\"14:00:00\"}," +
                                 "{\"type\":\"time\",\"time\":\"16:00:00\"}]'" +
            "/>" + ACTION_BTN_ELEMENT +
            "</form></div>";
    String presentationML = context.getPresentationML();
    assertEquals("PresentationML", expectedPresentationML, presentationML);

  }

  @Test
  public void testTimePickerTitleNewLine() throws Exception {
    String input = "<messageML>" +
            "<form id=\"" + formId + "\">" +
            "<time-picker " +
            "name=\"time-meeting\" " +
            "title=\"This is \\n a hint\"/>" +
            ACTION_BTN_ELEMENT + "</form></messageML>";

    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);

    String presentationML = context.getPresentationML();
    String timePickerRegex = ".*(\"time-picker-(.*?)\").*";
    Pattern pattern = Pattern.compile(timePickerRegex);
    Matcher matcher = pattern.matcher(presentationML);
    String uniqueLabelId = matcher.matches() ? matcher.group(2) : null;

    String EXPECTED_MARKDOWN = "Form (log into desktop client to answer):\n"
            + "---\n"
            + "(Time Picker:[This is \\n a hint])(Button:Send)\n"
            + "---\n";
    String expectedPresentationML = String.format("<div data-format=\"PresentationML\" data-version=\"2.0\">" +
            "<form id=\"timepicker-form\">" +
            "<div class=\"time-picker-group\" data-generated=\"true\">" +
            "<span class=\"info-hint\" data-target-id=\"time-picker-%s\" data-title=\"This is \\n a hint\"></span>" +
            "<input type=\"time\" name=\"time-meeting\" id=\"time-picker-%s\"/>" +
            "</div>%s</form></div>", uniqueLabelId, uniqueLabelId, ACTION_BTN_ELEMENT );

    assertEquals("Markdown", EXPECTED_MARKDOWN, context.getMarkdown());
    assertEquals("PresentationML", expectedPresentationML, presentationML);
  }

  @Test
  public void testTimePickerWithoutRequiredName() throws Exception {
    String input = "<messageML>" +
            "<form id=\"form\">" +
            "<time-picker min=\"08:00:00\" max=\"18:00:00\" />" + ACTION_BTN_ELEMENT + "</form></messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("The attribute \"name\" is required");
    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testTimePickerWrongValueAttr() throws Exception {
    String input = "<messageML>" +
            "<form id=\"form\">" +
            "<time-picker name=\"meeting-time\" value=\"18:00:00 a\" />" + ACTION_BTN_ELEMENT + "</form></messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Attribute \"value\" has invalid time format, only HH:mm:ss format is allowed");
    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testTimePickerWith12HoursFormatAttr() throws Exception {
    String input = "<messageML>" +
            "<form id=\"" + formId + "\">" +
            "<time-picker " +
            "name=\"time-meeting\" " +
            "format=\"hh:mm a\"/>" +
            ACTION_BTN_ELEMENT + "</form></messageML>";

    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
    String presentationML = context.getPresentationML();

    String expectedPresentationML = String.format("<div data-format=\"PresentationML\" data-version=\"2.0\">" +
            "<form id=\"" + formId + "\">" +
            "<input type=\"time\" " +
            "name=\"time-meeting\" " +
            "data-format=\"hh:mm a\"/>%s</form></div>", ACTION_BTN_ELEMENT);

    assertEquals("PresentationML", expectedPresentationML, presentationML);
  }

  @Test
  public void testTimePickerWrongFormatAttr() throws Exception {
    String input = "<messageML>" +
            "<form id=\"form\">" +
            "<time-picker name=\"meeting-time\" format=\"dd:mm:ss a\" />" + ACTION_BTN_ELEMENT + "</form></messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Attribute \"format\" contains an unsupported time format, only 'h', 'm' " +
            "'s' and 'a' are supported with ':' or space as separator");
    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testTimePickerWrongStrictAttr() throws Exception {
    String input = "<messageML>" +
            "<form id=\"form\">" +
            "<time-picker name=\"meeting-time\" strict=\"s\" />" + ACTION_BTN_ELEMENT + "</form></messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Attribute \"strict\" of element \"time-picker\" can only be one of the " +
            "following values: [true, false].");
    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testTimePickerWrongRequiredAttr() throws Exception {
    String input = "<messageML>" +
            "<form id=\"form\">" +
            "<time-picker name=\"meeting-time\" required=\"s\" />" + ACTION_BTN_ELEMENT + "</form></messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Attribute \"required\" of element \"time-picker\" can only be one of the " +
            "following values: [true, false].");
    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testTimePickerWrongMinFormat() throws Exception {
    String input = "<messageML>" +
            "<form id=\"form\">" +
            "<time-picker name=\"meeting-time\" min=\"10:56:10 a\" />" + ACTION_BTN_ELEMENT + "</form></messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Attribute \"min\" has invalid time format, only HH:mm:ss format is allowed");
    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testTimePickerStepOutOfRange() throws Exception {
    String input = "<messageML>" +
            "<form id=\"form\">" +
            "<time-picker name=\"meeting-time\" step=\"100\" />" + ACTION_BTN_ELEMENT + "</form></messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Attribute \"step\" must be less than \"43200\" and more than \"600");
    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testTimePickerStepNotANumber() throws Exception {
    String input = "<messageML>" +
            "<form id=\"form\">" +
            "<time-picker name=\"meeting-time\" step=\"abc\" />" + ACTION_BTN_ELEMENT + "</form></messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Attribute \"step\" should be a number.");
    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testTimePickerWrongFormatDisabledTime() throws Exception {
    String input = "<messageML>" +
            "<form id=\"form\">" +
            "<time-picker name=\"meeting-time\" " +
            "disabled-time='[{\"from\": \"12:00:00\", \"to\": \"14:00 a\"}]'/>" +
            ACTION_BTN_ELEMENT + "</form></messageML>";

    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("\"14:00 a\" is not a valid time, only HH:mm:ss format is allowed");
    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testTimePickerWrongRangeDisabledTime() throws Exception {
    String input = "<messageML>" +
            "<form id=\"form\">" +
            "<time-picker name=\"meeting-time\" " +
            "disabled-time='[{\"from\": \"12:00:00\"}]'/>" +
            ACTION_BTN_ELEMENT + "</form></messageML>";
    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Time interval 'type' is unknown or null");
    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testTimePickerWrongTypeDisabledTime() throws Exception {
    String input = "<messageML>" +
            "<form id=\"form\">" +
            "<time-picker name=\"meeting-time\" " +
            "disabled-time='[{\"hour\": \"16:00:00\"}]'/>" +
            ACTION_BTN_ELEMENT + "</form></messageML>";
    expectedException.expect(InvalidInputException.class);
    expectedException.expectMessage("Error parsing json in attribute \"disabled-time\": Unrecognized field \"hour\"");
    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
  }

  @Test
  public void testEmptyMarkdown() throws Exception {
    String input = "<messageML><form id=\"" + formId + "\">"
            + "<time-picker name=\"meeting-time\"/>"
            + ACTION_BTN_ELEMENT + "</form></messageML>";
    String EXPECTED_MARKDOWN = "Form (log into desktop client to answer):\n"
            + "---\n"
            + "(Time Picker)(Button:Send)\n"
            + "---\n";
    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);
    assertEquals("Markdown", EXPECTED_MARKDOWN, context.getMarkdown());
  }

  @Test
  public void testMarkdownWithUnderscore() throws Exception {
    String input = "<messageML>" +
            "<form id=\"" + formId + "\">" +
            "<time-picker " +
            "name=\"time-meeting\" " +
            "title=\"This is_a_hint\"/>" +
            ACTION_BTN_ELEMENT + "</form></messageML>";

    context.parseMessageML(input, null, MessageML.MESSAGEML_VERSION);

    String EXPECTED_MARKDOWN = "Form (log into desktop client to answer):\n"
            + "---\n"
            + "(Time Picker:[This is\\_a\\_hint])(Button:Send)\n"
            + "---\n";

    assertEquals("Markdown", EXPECTED_MARKDOWN, context.getMarkdown());
  }

}
