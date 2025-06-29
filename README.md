# Java Query Language

This project is a **mini relational database engine** built in Java, using string processing and regex. 

## Main Features
- Table creation and deletion
- Row insertion, update, and deletion
- Filtering with custom expressions (e.g., `grade > 15`)
- Support for basic data types: `int`, `str`, `dbl`, `time`
- Grouping and counting based on field combinations
- Custom field selection and arithmetic operations in queries
- Interactive command-line interface
- Graceful error handling with meaningful messages

**Example Commands**:
```plaintext
create students[id int, name str, grade dbl, approved int]
add students{id=40211343, name='mani'}
get students(grade > 15.0)
set students(id=40210000){approved=1}
del students(name='mani', grade=18.5)   
```

##  Full Project Specification
For the full project instructions (in Persian), please refer to the link in the _Website_ section of this repository's About panel (top-right).
##