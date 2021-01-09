@echo off

REM --------------------------------------------------
REM Monster Trading Cards Game
REM --------------------------------------------------
title Monster Trading Cards Game
echo CURL Testing for Monster Trading Cards Game
echo.

REM --------------------------------------------------
echo 1) Create Users (Registration)
REM Create User
curl -X POST http://localhost:8080/api/users --header "Content-Type: application/json" -d "{\"username\":\"kienboec\", \"password\":\"daniel\"}"
echo.
curl -X POST http://localhost:8080/api/users --header "Content-Type: application/json" -d "{\"username\":\"altenhof\", \"password\":\"markus\"}"
echo.
curl -X POST http://localhost:8080/api/users --header "Content-Type: application/json" -d "{\"username\":\"admin\",    \"password\":\"istrator\"}"
echo.

echo should fail:
curl -X POST http://localhost:8080/api/users --header "Content-Type: application/json" -d "{\"username\":\"kienboec\", \"password\":\"daniel\"}"
echo.
curl -X POST http://localhost:8080/api/users --header "Content-Type: application/json" -d "{\"username\":\"kienboec\", \"password\":\"different\"}"
echo. 
echo.

REM --------------------------------------------------
echo 2) Login Users
curl -X POST http://localhost:8080/api/sessions --header "Content-Type: application/json" -d "{\"username\":\"kienboec\", \"password\":\"daniel\"}"
echo.
curl -X POST http://localhost:8080/api/sessions --header "Content-Type: application/json" -d "{\"username\":\"altenhof\", \"password\":\"markus\"}"
echo.
curl -X POST http://localhost:8080/api/sessions --header "Content-Type: application/json" -d "{\"username\":\"admin\",    \"password\":\"istrator\"}"
echo.

echo should fail:
curl -X POST http://localhost:8080/api/sessions --header "Content-Type: application/json" -d "{\"username\":\"kienboec\", \"password\":\"different\"}"
echo.
echo.

REM --------------------------------------------------
echo 4) acquire packages kienboec
curl -X POST http://localhost:8080/api/transactions/packages --header "Content-Type: application/json" --header "Authorization: Bearer kienboec-mtcgToken" -d ""
echo.
curl -X POST http://localhost:8080/api/transactions/packages --header "Content-Type: application/json" --header "Authorization: Bearer kienboec-mtcgToken" -d ""
echo.
curl -X POST http://localhost:8080/api/transactions/packages --header "Content-Type: application/json" --header "Authorization: Bearer kienboec-mtcgToken" -d ""
echo.
curl -X POST http://localhost:8080/api/transactions/packages --header "Content-Type: application/json" --header "Authorization: Bearer kienboec-mtcgToken" -d ""
echo.
echo should fail (no money):
curl -X POST http://localhost:8080/api/transactions/packages --header "Content-Type: application/json" --header "Authorization: Bearer kienboec-mtcgToken" -d ""
echo.
echo open packages kienboec
curl -X POST http://localhost:8080/api/packages --header "Content-Type: application/json" --header "Authorization: Bearer kienboec-mtcgToken" -d ""
echo.
curl -X POST http://localhost:8080/api/packages --header "Content-Type: application/json" --header "Authorization: Bearer kienboec-mtcgToken" -d ""
echo.
curl -X POST http://localhost:8080/api/packages --header "Content-Type: application/json" --header "Authorization: Bearer kienboec-mtcgToken" -d ""
echo.
curl -X POST http://localhost:8080/api/packages --header "Content-Type: application/json" --header "Authorization: Bearer kienboec-mtcgToken" -d ""
echo.
echo.

REM --------------------------------------------------
echo 5) acquire packages altenhof
curl -X POST http://localhost:8080/api/transactions/packages --header "Content-Type: application/json" --header "Authorization: Bearer altenhof-mtcgToken" -d ""
echo.
curl -X POST http://localhost:8080/api/transactions/packages --header "Content-Type: application/json" --header "Authorization: Bearer altenhof-mtcgToken" -d ""
echo.
curl -X POST http://localhost:8080/api/transactions/packages --header "Content-Type: application/json" --header "Authorization: Bearer altenhof-mtcgToken" -d ""
echo.
curl -X POST http://localhost:8080/api/transactions/packages --header "Content-Type: application/json" --header "Authorization: Bearer altenhof-mtcgToken" -d ""
echo.
echo should fail (no money):
curl -X POST http://localhost:8080/api/transactions/packages --header "Content-Type: application/json" --header "Authorization: Bearer altenhof-mtcgToken" -d ""
echo.
echo open packages altenhof
curl -X POST http://localhost:8080/api/packages --header "Content-Type: application/json" --header "Authorization: Bearer altenhof-mtcgToken" -d ""
echo.
curl -X POST http://localhost:8080/api/packages --header "Content-Type: application/json" --header "Authorization: Bearer altenhof-mtcgToken" -d ""
echo.
curl -X POST http://localhost:8080/api/packages --header "Content-Type: application/json" --header "Authorization: Bearer altenhof-mtcgToken" -d ""
echo.
curl -X POST http://localhost:8080/api/packages --header "Content-Type: application/json" --header "Authorization: Bearer altenhof-mtcgToken" -d ""
echo.
echo.

REM --------------------------------------------------
echo 8) show all acquired cards kienboec
curl -X GET http://localhost:8080/api/cards --header "Authorization: Bearer kienboec-mtcgToken"
echo.
echo should fail (no token)
curl -X GET http://localhost:8080/api/cards
echo.
echo.

REM --------------------------------------------------
echo 9) show all acquired cards altenhof
curl -X GET http://localhost:8080/api/cards --header "Authorization: Bearer altenhof-mtcgToken"
echo.
echo.

REM --------------------------------------------------
echo 10) show unconfigured deck
curl -X GET http://localhost:8080/api/decks --header "Authorization: Bearer kienboec-mtcgToken"
echo.
curl -X GET http://localhost:8080/api/decks --header "Authorization: Bearer altenhof-mtcgToken"
echo.
echo.

REM --------------------------------------------------
echo 11) configure deck
curl -X PUT http://localhost:8080/api/decks --header "Content-Type: application/json" --header "Authorization: Bearer kienboec-mtcgToken" -d "[1, 2, 3, 4]"
echo.
curl -X GET http://localhost:8080/api/decks --header "Authorization: Bearer kienboec-mtcgToken"
echo.
curl -X PUT http://localhost:8080/api/decks --header "Content-Type: application/json" --header "Authorization: Bearer altenhof-mtcgToken" -d "[21, 22, 23, 24]"
echo.
curl -X GET http://localhost:8080/api/decks --header "Authorization: Bearer altenhof-mtcgToken"
echo.
echo.
echo should fail and show original from before:
curl -X PUT http://localhost:8080/api/decks --header "Content-Type: application/json" --header "Authorization: Bearer altenhof-mtcgToken" -d "[21, 22, 23, 24]"
echo.
curl -X GET http://localhost:8080/api/decks --header "Authorization: Bearer altenhof-mtcgToken"
echo.
echo.
echo should fail ... only 3 cards set
curl -X PUT http://localhost:8080/api/decks --header "Content-Type: application/json" --header "Authorization: Bearer altenhof-mtcgToken" -d "[25, 26, 27]"
echo.


REM --------------------------------------------------
echo 12) show configured deck 
curl -X GET http://localhost:8080/api/decks --header "Authorization: Bearer kienboec-mtcgToken"
echo.
curl -X GET http://localhost:8080/api/decks --header "Authorization: Bearer altenhof-mtcgToken"
echo.
echo.

REM --------------------------------------------------
echo 14) edit user data
echo.
curl -X GET http://localhost:8080/api/users --header "Authorization: Bearer kienboec-mtcgToken"
echo.
curl -X GET http://localhost:8080/api/users --header "Authorization: Bearer altenhof-mtcgToken"
echo.
curl -X PUT http://localhost:8080/api/users --header "Content-Type: application/json" --header "Authorization: Bearer kienboec-mtcgToken" -d "{\"username\": \"kienboeckNew\",  \"password\": \"newPw\"}"
echo.
curl -X PUT http://localhost:8080/api/users --header "Content-Type: application/json" --header "Authorization: Bearer altenhof-mtcgToken" -d "{\"username\": \"altenhoferNew\", \"password\": \"newPw\"}"
echo.
curl -X GET http://localhost:8080/api/users --header "Authorization: Bearer kienboec-mtcgToken"
echo.
curl -X GET http://localhost:8080/api/users --header "Authorization: Bearer altenhof-mtcgToken"
echo.
echo.
echo should fail:
curl -X GET http://localhost:8080/api/users --header "Authorization: Bearer kienboec-mtcgToken"
echo.
curl -X GET http://localhost:8080/api/users --header "Authorization: Bearer altenhof-mtcgToken"
echo.
curl -X PUT http://localhost:8080/api/users --header "Content-Type: application/json" --header "Authorization: Bearer kienboec-mtcgToken" -d "{\"username\": \"kienboeckNew\",  \"password\": \"newPw\"}"
echo.
curl -X PUT http://localhost:8080/api/users --header "Content-Type: application/json" --header "Authorization: Bearer altenhof-mtcgToken" -d "{\"username\": \"altenhoferNew\", \"password\": \"newPw\"}"
echo.
curl -X GET http://localhost:8080/api/users --header "Authorization: Bearer kienboec-mtcgToken"
echo.
curl -X GET http://localhost:8080/api/users --header "Authorization: Bearer altenhof-mtcgToken"
echo.
echo.

REM --------------------------------------------------
echo 15) stats
curl -X GET http://localhost:8080/api/users/stats --header "Authorization: Bearer kienboec-mtcgToken"
echo.
curl -X GET http://localhost:8080/api/users/stats --header "Authorization: Bearer altenhof-mtcgToken"
echo.
echo.

REM --------------------------------------------------
echo 16) scoreboard
curl -X GET http://localhost:8080/api/users/elo --header "Authorization: Bearer kienboec-mtcgToken"
echo.
echo.

REM --------------------------------------------------
echo 17) battle
echo.
start /b "kienboec battle" curl -X POST http://localhost:8080/api/battles --header "Authorization: Bearer kienboec-mtcgToken"
echo.
start /b "altenhof battle" curl -X POST http://localhost:8080/api/battles --header "Authorization: Bearer altenhof-mtcgToken"
echo.
ping localhost -n 10 >NUL 2>NUL

REM --------------------------------------------------
echo 18) Stats 
echo kienboec
curl -X GET http://localhost:8080/api/users/stats --header "Authorization: Bearer kienboec-mtcgToken"
echo.
echo altenhof
curl -X GET http://localhost:8080/api/users/stats --header "Authorization: Bearer altenhof-mtcgToken"
echo.
echo.

REM --------------------------------------------------
echo 19) scoreboard
curl -X GET http://localhost:8080/api/users/elo --header "Authorization: Bearer kienboec-mtcgToken"
echo.
echo.

REM --------------------------------------------------
echo 20) trade
echo check trading deals
curl -X GET http://localhost:8080/api/tradings --header "Authorization: Bearer kienboec-mtcgToken"
echo.
echo create trading deal
curl -X POST http://localhost:8080/api/tradings --header "Content-Type: application/json" --header "Authorization: Bearer kienboec-mtcgToken" -d "{\"cardId\": 1, \"type\": \"MONSTER\", \"minDamage\": 1}"
echo.
echo check trading deals
curl -X GET http://localhost:8080/api/tradings --header "Authorization: Bearer kienboec-mtcgToken"
echo.
curl -X GET http://localhost:8080/api/tradings --header "Authorization: Bearer altenhof-mtcgToken"
echo.
echo delete trading deals
curl -X DELETE http://localhost:8080/api/tradings/1 --header "Authorization: Bearer kienboec-mtcgToken"
echo.
echo.

REM --------------------------------------------------
echo 21) check trading deals
curl -X GET http://localhost:8080/api/tradings  --header "Authorization: Bearer kienboec-mtcgToken"
echo.
curl -X POST http://localhost:8080/api/tradings --header "Content-Type: application/json" --header "Authorization: Bearer kienboec-mtcgToken" -d "{\"cardId\": 2, \"type\": \"MONSTER\", \"minDamage\": 1}"
echo check trading deals
curl -X GET http://localhost:8080/api/tradings  --header "Authorization: Bearer kienboec-mtcgToken"
echo.
curl -X GET http://localhost:8080/api/tradings  --header "Authorization: Bearer altenhof-mtcgToken"
echo.
echo try to trade with yourself (should fail)
curl -X POST http://localhost:8080/api/tradings/2/3 --header "Content-Type: application/json" --header "Authorization: Bearer kienboec-mtcgToken" -d ""
echo.
echo try to trade 
echo.
curl -X POST http://localhost:8080/api/tradings/2/25 --header "Content-Type: application/json" --header "Authorization: Bearer altenhof-mtcgToken" -d ""
echo.
curl -X GET http://localhost:8080/api/tradings --header "Authorization: Bearer kienboec-mtcgToken"
echo.
curl -X GET http://localhost:8080/api/tradings --header "Authorization: Bearer altenhof-mtcgToken"
echo.

REM --------------------------------------------------
echo end...

REM this is approx a sleep 
ping localhost -n 10000000 >NUL 2>NUL
@echo on
