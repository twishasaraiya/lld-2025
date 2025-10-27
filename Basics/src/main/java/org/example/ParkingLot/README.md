Problem Statement
You need to design and implement a Multi-Level Parking Lot System for a large shopping mall. The system should handle vehicle parking, spot allocation, and payment processing efficiently.

Requirements
Phase 1 - Core Functionality (Must Implement - 35 minutes)
Functional Requirements:

Multi-level parking structure - 4 floors, each with different zones
Vehicle type support:

Motorcycle (1 spot)
Car (1 spot)
SUV/Truck (2 spots)
Bus (4 spots)


Parking spot management:

Find and allocate appropriate spots for vehicles
Release spots when vehicles leave
Track occupied vs available spots in real-time


Basic payment processing:

Hourly rate-based charging
Generate parking tickets with entry time
Calculate fees on exit



Non-Functional Requirements:

Thread-safe operations (multiple entry/exit gates)
Efficient spot finding (O(1) or O(log n) complexity)
Memory efficient for 1000+ parking spots
Extensible design for future features

Phase 2 - Advanced Features (If time permits - 20 minutes)

Dynamic pricing:

Peak/off-peak hours
Different rates for different zones (VIP, handicapped, regular)
Weekend vs weekday pricing


Real-time notifications:

Notify when parking is almost full (>90%)
Alert security for overstayed vehicles
Update digital displays about availability


Reservation system:

Pre-book parking spots
Hold spots for 15 minutes after booking