### How to Run

1. Extract contents of zip file into a folder.
2. Open Package Room log on Teams and navigate to the page you want.
3. `Ctrl+A` to select all entries on the page, and `Ctrl+C` to copy.
4. Paste this directly into the file titled `raw.config`.
5. Run the program. You must have Java installed on your current machine.
    * On Windows, double click on `run.bat`.
    * On Linux or Mac, open up a terminal window in the directory with the extracted files, run the command `chmod +x run.sh` (this only needs to be done once), then use `./run.sh`.
6. Follow any instructions the program tells you. It will do its best to look up known organization emails, but if it can't find the email, you will have to manually look it up in Hopkins Groups.
7. Remember to mark the packages as "email sent" by typing anything into the "Email Sent by Manager" column on Teams afterwards!
8. The results are located in `out.txt`. They are nicely formatted in a way so you can copy the first line into Sender, second line into Email Header, body into Email Body. There is no way to automatically send all of them out (Hopkins's fault).

### How to Change a Group's Email
1. Open up `orgs.txt`.
2. Emails are stored as mappings to organization names. Misspellings will have multiple mappings to the same email, so search for the email if you want to change or remove the entry. Each mapping is separated by a `TAB` character, **NOT A SPACE!**

### How to Add Alternate Emails (to CC)
1. Open up `alts.txt`.
2. Type the organization's main email (if it doesn't already exist), and add a tab-separated list of alternate emails afterwards. The alternate emails will be conveniently placed with the main email during email generation so you can easily CC them.<br>

**Examples:**<br>
`main@jhu.edu	secondary@jhu.edu` will email `main` and CC `secondary`.<br>
`bobby@jhu.edu	sally@jhu.edu	timothy@jhu.edu` will email `bobby` and CC both `sally` and `timothy`.<br>

### How to Change Email Format
* `format.txt` is the basic format.
* `format2.txt` is the format for when organizations have no new packages, but have old packages that haven't been picked up.
* `format_prev.txt` is the format that gets added to `format.txt` as **%PREVIOUS%** when the organization has un-picked-up packages.

Wildcards are special words that get swapped with variable information when emails are generated.
The available wildcards are:
* **%ORG%** - the organization's name
* **%NUM%** - the number of new packages received
* **%LOCATION%** - a list of the senders of the packages
* **%IDS%** - a list of package IDs, one per line
* **%DELIVERY_DATE** - the date, MM/DD/YYYY, of when the first package in the list of packages was received
* **%PICKUP_BY%** - the date, WEEKDAY MM/DD/YYYY, one week from the current day