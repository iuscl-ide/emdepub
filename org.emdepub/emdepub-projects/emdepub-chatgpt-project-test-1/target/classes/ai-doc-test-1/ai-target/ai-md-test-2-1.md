# Introduction

## Motivation

Today, log file management remains a challenging task, which has resulted in the need for an application to simplify the process.
In maintenance teams, employees typically arrive at the office and check for any incidents. If they find one, they usually download the log file from a remote network location and open it in a text editor such as *Notepad++*. They then use the "**Ctrl-F**" function to search for the keyword "**ERROR**" and proceed to locate and address each error found in the file.
Performing this task on a daily basis can lead to a deep understanding of the log file format and the specific search terms required to identify the root cause of an issue. However, due to the fact that logs are essentially just plain text, and different applications have their own unique logging styles, it can be a challenging and time-consuming process.

There are certain similarities shared by log files:

### Lines/Records

A log file is composed of lines, which are essentially paragraphs ending with a line break represented by "**\R**" (`CR`, `LF`, `CRLF`). Meanwhile, a record refers to a single entry or piece of information that is recorded in the log at one time. A record can consist of one or multiple lines, such as a simple information on a single line, or an exception containing multiple lines for the stack trace. Some modern logs may even have simple information spread across multiple lines, such as one line for the date and level, and another line for the message. Alternatively, an entire record may be presented in a multi-line *JSON* or *XML* layout format that is neatly formatted for improved readability. In other scenarios, a *JSON* or *XML* record must be contained within a single line to ensure proper processing by *Kibana*.

> For log examples, please see the help topic "`Log Examples`"

### Identifiers

To identify the start of each log record, there should be a consistent marker that separates records from one another. This enables the end of a record to be identified just before the start of the next. In many cases, the identifier is a date and time stamp.

### Levels

Each record should be assigned a level, such as INFO, WARNING, ERROR, DEBUG, or a variation thereof, to group similar records together. This not only helps to locate a specific record when searching through logs, but also enables selective logging of specific events, rather than logging everything which can result in very large logs. While each record should ideally have a level, it is not always enforced, resulting in some logs with records lacking a level.

## Implementation

Next, we will explore ways to make these log file characteristics as visible as possible.

### Lines/Records/Fields Views

4y0cpfv0 -- There are three available options for viewing a log file: **"Lines"** (similar to a standard text file), **"Records"** (organized by log information), and **"Fields"** (displaying record values).

### Lines View

### Records View

### Fields View

### Levels

### Filters

### Fields and Map Fields

### Search

|                           **Log Values** - Because logs are hard                           |
|:------------------------------------------------------------------------------------------:|
|                                    log-values.com 2023                                     |
|                         *(this text was reformatted with ChatGPT)*                         |
| __________________________________________________________________________________________ |

