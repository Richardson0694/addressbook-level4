package seedu.carvicim.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.carvicim.logic.parser.CliSyntax.PREFIX_JOB_NUMBER;

import java.io.IOException;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import javafx.collections.ObservableList;

import seedu.carvicim.commons.GmailAuthenticator;
import seedu.carvicim.commons.core.Messages;
import seedu.carvicim.logic.commands.exceptions.CommandException;
import seedu.carvicim.model.ModelManager;
import seedu.carvicim.model.job.Job;
import seedu.carvicim.model.job.JobNumber;
import seedu.carvicim.model.person.Employee;
import seedu.carvicim.model.person.UniqueEmployeeList;

//@@author charmaineleehc
/**
 * Sends email to employee.
 */
public class EmailCommand extends Command {

    public static final String COMMAND_WORD = "email";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": email employees about job details.\n"
            + "Parameters: "
            + PREFIX_JOB_NUMBER + "JOB_NUMBER "
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_JOB_NUMBER + "12 ";

    public static final String MESSAGE_SUCCESS = "The email has been successfully sent to your employee!";

    private static final String CARVICIM_EMAIL = "me";
    private static final String EMAIL_SUBJECT = "Job details";

    private final JobNumber jobNumber;

    /**
     * Creates an EmailCommand
     * @param jobNumber of the job details to be sent via email to the employee(s) in charge
     */
    public EmailCommand(JobNumber jobNumber) {
        this.jobNumber = jobNumber;
    }

    public UniqueEmployeeList getListOfEmployeesOfJob() throws CommandException {
        ObservableList<Job> filteredJobList = model.getFilteredJobList();

        if (jobNumber.asInteger() > filteredJobList.size() || jobNumber.asInteger() <= 0) {
            throw new CommandException(Messages.MESSAGE_INVALID_JOB_NUMBER);
        }

        UniqueEmployeeList listOfEmployeesOfJob = filteredJobList.get(jobNumber.asInteger() - 1).getAssignedEmployees();

        return listOfEmployeesOfJob;
    }

    /**
     * Sends an email to each employee in {@code listOfEmployeesOfJob}
     * @param listOfEmployeesOfJob
     * @throws MessagingException
     * @throws IOException
     */
    public void sendEmails(UniqueEmployeeList listOfEmployeesOfJob) throws MessagingException, IOException {
        ObservableList<Job> filteredJobList = model.getFilteredJobList();

        for (Employee employee : listOfEmployeesOfJob) {
            Job job = filteredJobList.get(jobNumber.asInteger() - 1);

            String emailContent = "Hi " + employee.getName().toString() + ",\n\n"
                    + "Thank you for all your hard work thus far.\n\n"
                    + "Here are the job details for the job assigned to you on " + job.getDate().toString() + ":\n"
                    + "Client: " + job.getClient().getName().toString() + "\n"
                    + "Client's phone number: " + job.getClient().getPhone().toString() + "\n"
                    + "Vehicle number: " + job.getVehicleNumber().toString() + "\n"
                    + "Remarks: " + job.getRemarkList().getRemarks().toString() + "\n\n"
                    + "Thank you, and happy servicing!\n\n";

            GmailAuthenticator gmailAuthenticator = new GmailAuthenticator();
            MimeMessage mimeMessage = ModelManager.createEmail(
                    employee.getEmail().toString(), CARVICIM_EMAIL, EMAIL_SUBJECT, emailContent);
            ModelManager.sendMessage(gmailAuthenticator.getGmailService(), CARVICIM_EMAIL, mimeMessage);
        }
    }

    @Override
    public CommandResult execute() throws CommandException {
        requireNonNull(model);

        UniqueEmployeeList listOfEmployeesOfJob = getListOfEmployeesOfJob();

        try {
            sendEmails(listOfEmployeesOfJob);
        } catch (MessagingException | IOException e) {
            System.exit(1);
        }

        return new CommandResult(MESSAGE_SUCCESS);

    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof EmailCommand // instanceof handles nulls
                && jobNumber.equals(((EmailCommand) other).jobNumber));
    }

}
