# GeofencingAPI

## Overview
This is an Android Java application using google's geofencing API. It records real-time entry and exit of a single or multiple geofences and stores records in transitions' database using Android's Content Provider.
Geofences are added by users of the application with a fixed radius of 100m.


## Screenshots

| <img src="https://github.com/user-attachments/assets/e66d80b4-a851-4711-95d0-8dd4770b3777" width="200" />
<img src="https://github.com/user-attachments/assets/231db136-a937-4bb1-b324-ac4f444825f8" width="200" />
<img src="https://github.com/user-attachments/assets/42b27dc3-c3e8-4669-b3ab-4bae4e72fcbc" width="200" />
<img src="https://github.com/user-attachments/assets/026478da-4f61-4b23-b780-d3c8b7c34567" width="200" />
<img src="https://github.com/user-attachments/assets/b554ab45-664e-4871-a8fb-0cb2a8b6238c" width="200" />
<img src="https://github.com/user-attachments/assets/dbcc1a6f-842b-4d5b-b63c-a9ad2fa12070" width="200" />
<img src="https://github.com/user-attachments/assets/5b18bbb4-4757-4ee0-8066-d5d116a4ccb8" width="200" />
<img src="https://github.com/user-attachments/assets/30ffad0f-3448-418b-a990-7744546386cf" width="200" />


## Usage
1. **Define geofences** on the map.
2. **Start monitoring** for geofence transitions.
3. View logged events for entry and exit transitions.
4. Stop monitoring and drop tables for erasing history.

---

## Future Enhancements
- Foreground geofence monitoring using Service class.
- Advanced notifications for geofence events.
- Improved UI for geofence management.
