@echo off
setlocal

echo ========================================
echo Building MSI Installer for Hotel Reservation System
echo ========================================

echo Step 1: Building application JAR...
call mvn clean package

if errorlevel 1 (
    echo ERROR: Maven build failed!
    pause
    exit /b 1
)

echo Step 2: Creating MSI installer with icon...
jpackage ^
  --name "Hotel Reservation System" ^
  --input target ^
  --main-jar HotelReservationSystem-1.0.0.jar ^
  --main-class com.hotel.Main ^
  --type msi ^
  --dest . ^
  --vendor "Hotel Grand" ^
  --app-version "1.0.0" ^
  --description "Professional Hotel Reservation Management System" ^
  --copyright "Copyright 2025 Hotel Grand" ^
  --win-shortcut ^
  --win-menu ^
  --win-menu-group "Hotel Reservation System" ^
  --win-dir-chooser ^
  --win-per-user-install ^
  --icon "src\main\resources\icon.ico" ^
  --install-dir "Hotel Reservation System"

if errorlevel 1 (
    echo ERROR: jpackage failed!
    echo.
    echo Possible solutions:
    echo 1. Make sure you're using JDK 14+
    echo 2. Run Command Prompt as Administrator
    echo 3. Check Java is in PATH: jpackage --version
    echo 4. Verify icon file exists: src\main\resources\icon.ico
    echo.
    pause
    exit /b 1
)

echo Step 3: Creating distribution package...
if exist "Distribution_Package" rmdir /s /q "Distribution_Package"
mkdir "Distribution_Package"
move "Hotel Reservation System-1.0.0.msi" "Distribution_Package\"
copy "src\main\resources\firebase-config.json" "Distribution_Package\"

echo Creating README file...
echo Hotel Reservation System > "Distribution_Package\README.txt"
echo ======================= >> "Distribution_Package\README.txt"
echo. >> "Distribution_Package\README.txt"
echo Installation Instructions: >> "Distribution_Package\README.txt"
echo 1. Run "Hotel Reservation System-1.0.0.msi" >> "Distribution_Package\README.txt"
echo 2. Follow the installation wizard >> "Distribution_Package\README.txt"
echo 3. Launch from Start Menu or Desktop >> "Distribution_Package\README.txt"
echo. >> "Distribution_Package\README.txt"
echo System Requirements: >> "Distribution_Package\README.txt"
echo - Windows 10/11 (64-bit) >> "Distribution_Package\README.txt"
echo - 2GB RAM >> "Distribution_Package\README.txt"
echo - 200MB free space >> "Distribution_Package\README.txt"
echo. >> "Distribution_Package\README.txt"
echo Features: >> "Distribution_Package\README.txt"
echo - Guest Management >> "Distribution_Package\README.txt"
echo - Room Management >> "Distribution_Package\README.txt"
echo - Booking System >> "Distribution_Package\README.txt"
echo - Payment Processing >> "Distribution_Package\README.txt"
echo - Reports Generation >> "Distribution_Package\README.txt"
echo. >> "Distribution_Package\README.txt"
echo Support: support@hotelgrand.com >> "Distribution_Package\README.txt"

echo.
echo ========================================
echo SUCCESS: Professional MSI Installer Created!
echo ========================================
echo.
echo Files created in 'Distribution_Package' folder:
echo   - "Hotel Reservation System-1.0.0.msi"
echo   - firebase-config.json
echo   - README.txt
echo.
echo To distribute to users:
echo   1. Send the entire 'Distribution_Package' folder
echo   2. Users run the .msi file to install
echo   3. No Java required on user's computer!
echo.
echo Installation locations:
echo   - Start Menu: Hotel Reservation System
echo   - Program Files: C:\Program Files\Hotel Reservation System
echo   - Desktop shortcut (optional during installation)
echo.
pause