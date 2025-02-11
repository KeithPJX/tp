package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import seedu.address.commons.core.Messages;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.person.Survey;

/**
 * Marks the specified survey of a person as done.
 */
public class MarkCommand extends Command {
    public static final String COMMAND_WORD = "mark";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Marks survey in the person identified by the index number in the displayed person list as completed"
            + "Parameters: INDEX (must be a positive integer), s/NAME OF SURVEY\n" + "Example: " + COMMAND_WORD
            + " 1 s/Shopping survey";

    public static final String MESSAGE_MARK_PERSON_SUCCESS = "Marked survey as done: %1$s";
    public static final String MESSAGE_INVALID_SURVEY = "Wrong survey. Please type in the correct survey name";

    private final Index targetIndex;
    private final Survey targetSurvey;

    /**
     * Creates a MarkCommand to mark the specified survey
     *
     * @param index
     * @param survey
     */
    public MarkCommand(Index index, Survey survey) {
        targetIndex = index;
        targetSurvey = survey;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person targetPerson = lastShownList.get(targetIndex.getZeroBased());
        Set<Survey> surveySet = targetPerson.getSurveys();

        if (!surveySet.stream().anyMatch(x -> x.survey.equals(targetSurvey.survey))) {
            throw new CommandException(MESSAGE_INVALID_SURVEY);
        }

        Person editedPerson = createMarkedPerson(targetPerson, surveySet);
        model.setPerson(targetPerson, editedPerson);
        model.commitSurvin();
        return new CommandResult(String.format(MESSAGE_MARK_PERSON_SUCCESS, editedPerson));
    }

    private Person createMarkedPerson(Person targetPerson, Set<Survey> surveySet) {
        Set<Survey> newSurveySet = new HashSet<>();

        for (Survey s : surveySet) {
            if (s.survey.equals(targetSurvey.survey)) {
                Survey newSurvey = new Survey(targetSurvey.survey, true);
                newSurveySet.add(newSurvey);
            } else {
                newSurveySet.add(s);
            }
        }

        Person editedPerson = new Person(targetPerson.getName(), targetPerson.getPhone(), targetPerson.getEmail(),
            targetPerson.getAddress(), targetPerson.getGender(), targetPerson.getBirthdate(),
            targetPerson.getRace(), targetPerson.getReligion(), newSurveySet, targetPerson.getTags());
        return editedPerson;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        if (!(other instanceof MarkCommand)) {
            return false;
        }

        MarkCommand otherMarkCommand = (MarkCommand) other;

        return targetIndex.equals(otherMarkCommand.targetIndex) && targetSurvey.equals(otherMarkCommand.targetSurvey);
    }
}
