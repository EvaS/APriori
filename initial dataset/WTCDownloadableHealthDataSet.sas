******************Instructions for accessing WTCHR Downloadable Health Dataset******************

FILENAME: WTCHR Downloadable Health Data Set

1) Save the dataset and this program to your computer.
2) Change the pathnames and libname statements to reflect
where you have saved the data.

This program attaches the formats to the library name, and also 
provides sample code to use when analyzing survey data.
 
For more information, please contact:
NYC Department of Health & Mental Hygiene
Bureau of World Trade Center Health Registry
email : wtchr@health.nyc.gov 
**********************************************************************;

libname practice 'M:\Special Projects\PUDS III'; *(Change To Indicate Where Datafile Is saved);


proc format;

* Format for Numeric YES/NO Variables;
value ynf 0, 2='No' 
		  1='Yes'; 

*Format for Character YES/NO Variables;
value $C_YNF '0', '2' = 'No'
                '1 '= 'Yes';

value $Gender '1'='Male'
			  '2'='Female';

value agegrpf 1 = '<18 years' 
			  2 = '18-24 years' 
			  3 = '25-44 years' 
			  4 = '45-64 years' 
   			  5 = '65+ years' ;

value Education
			1='Grades Less Than High School'
			2='High School or GED'
			3='Some College'
		    4='College +';
			

value lang3_g   1 = 'English'
			    2 = 'Spanish'
			    3, 4 = 'Chinese'  
			    19, 24, 27, 37 = 'Other';

value census_g  1 = 'White (non Hispanic)'
                2 = 'Black or African American (non-Hispanic)'
			    3 = 'Hispanic or Latino(any race)'
                4 = 'Asian (includes Native Hawaiian/Pacific Islander)'
                5, 6 = 'Other'; 

value $ marital_g 1 = 'Married'
                  2, 6 = 'Not Married'
				  4, 5 = 'Divorced/Separated'
				  3 = 'Widowed';

value income_g  1= ' Less than $25,000'
                  2= ' $25,000 to less than $75,000'
                  3= ' $75,000 to less than $150,000'
                  4= ' $150,000 or More';
                
value smoke  	0 = 'Never smoked'
                1 = 'Former smoker'
			    2 = 'Current smoker';


value hor_event 1= 'No Events'
                2= 'Any Events';
		
value retlive
				1 = 'Never Returned'
				2 = 'Less than 7 Days'
				3 = '8 Days to 3 Months'
				4 = 'More than 3 Months';
				

value $howoften  1='All of the time'
                  2='Most of the time'
				  3='Sometimes'
				  4='Not at all';


value stcode_g  1 = 'New York City Resident'
				2 = 'Not New York City Resident but in TriState Area'
				3 = 'Resident Outside of TriState Area';
				
run;

*Call in the data;
data public_use_data_set;
set practice.puds_3_final;
run;

*Sample Code for running a frequency for your entire dataset; 
proc freq data=public_use_data_set;
run;

*Sample Code for Procedure Contents- See what variables are in your data set.;
proc contents data=public_use_data_set;
run;

* Sample Code for procedure print.;
proc print data = public_use_data_set (obs = 50);
var age_911grp any_injury breath_nw census_race depression_nw dust;
run;
