a) Implement a function that given a list of tennis court bookings with start and finish times, returns a plan assigning each booking to a specific court, ensuring each court is used by only one booking at a time and using the minimum amount of courts with unlimites number of courts available.  
An example of the booking record might look like

	Class BookingRecord:  
	Id: int//ID of the booking  
	Start_time: int  
	Finish_time: int
	
	And our function is going to look like:
	
	List assignCourts(List bookingRecords)
	
	b) After each booking, a fixed amount of time, X, is needed to maintain the court before it can be rented again  
	c) Court only need maintainenece after X amount of usage  
	How would you modify the code if each court also had a Y maintainence time that occurred after X bookings?  
	The function should now become something like  
	Def assign_court_with_maintainence(booking_records: list{BookingRecord],
	
	Maintainence_time: int,
	
	Durability: int) -> list[Court]:  
	d) The original problem can be made simpler by removing the “assigning each booking to a specific court” part. The candidate needs to find the minimum number of courts needed to accommodate all the bookings  
	e) Check if booking conflict - Write a function that if given two bookings to check if they conflict with each other