create database studentperformancetracker;
use  studentperformancetracker;
create table  user(id int auto_increment primary key,username varchar(50) not null unique,password varchar(100),role varchar(20),enabled boolean default true );
create table student(id int auto_increment primary key,name varchar(100),roll_number varchar(50),user_id int,foreign key(user_id) references user(id) on delete cascade);
create table faculty( id int auto_increment primary key, name varchar(100),department varchar(100), email varchar(100),user_id int, foreign key(user_id) references user(id) on delete cascade);
create table attendance(id int auto_increment primary key,student_id int,faculty_id int,date Date,status varchar(20) check (status in ('Present','Absent')),
foreign key(student_id) references student(id) on delete cascade,
foreign key(faculty_id) references faculty(id) on delete cascade);

create table performance(id int auto_increment primary key,student_id int,faculty_id int,
subject varchar(100),marks int,comments text,
FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    FOREIGN KEY (faculty_id) REFERENCES faculty(id) ON DELETE CASCADE
);

DROP TABLE IF EXISTS performance;
DROP TABLE IF EXISTS attendance;
DROP TABLE IF EXISTS faculty;
DROP TABLE IF EXISTS student;
DROP TABLE IF EXISTS user;

Select * from user;
Select * from student;
Select * from faculty;
