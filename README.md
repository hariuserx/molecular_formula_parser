# molecular_formula_parser
A generic formula parser for molecules using DP.


Start the Spring application and visit `http://localhost:8080`. To see the formula brekup make a get call with route as `parse` and the next path variable is the formula.

Example:<br>
http://localhost:8080/parse/NACL : Generates `NA*CL*`. 

The parser is case sensitive.
http://localhost:8080/parse/NAli: Generate `N*Al*i*` and http://localhost:8080/parse/Nali : Generates `Na*li*` and http://localhost:8080/parse/nacl : Generates `na*cl*`.

It also accepts numbers
http://localhost:8080/parse/Nal2cl334naclna: Generates `N*al*2*cl*334*na*cl*na*`

Define you custom fragments and their mass in src/main/resources/static/AtomicMassData.plg. 
