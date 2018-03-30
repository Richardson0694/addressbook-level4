package seedu.address.logic.commands;

import java.io.IOException;

import seedu.address.commons.GmailAuthenticator;

/**
 * Directs user to the login page of Gmail for user to log in.
 */
public class LoginCommand extends UndoableCommand {

    public static final String COMMAND_WORD = "login";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Logs in user into Gmail account.\n"
            + "Example: " + COMMAND_WORD;

    public static final String MESSAGE_SUCCESS = "You have successfully logged into your Gmail account!";

    @Override
    public CommandResult executeUndoableCommand() {
        try {
            new GmailAuthenticator();
        } catch (IOException ioe) {
            System.exit(1);
        }
        return new CommandResult(MESSAGE_SUCCESS);
    }

}