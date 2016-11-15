Go to 
C:\CommonProgs\TrickSQL\ConfigFiles\Jnj\tasks\SFDC\Customs\lf.int.geolocationUpdate_java

-----------------------------------------------------------
Account has the field called Geolocation Status with values:
o	OK: Geolocation done, all fine;
o	ZERO_RESULTS: No address found, the address of the account is not correct;
o	FOUND:<number>: the account address is not unequivocal, a few possible locations were found. The 1st that was found was used;
o	<Blank>: The account to be calculated. The value becomes blank automatically once account address is modified;
o	MANUAL: This account will not be updated. Useful for accounts that need to be excluded from automatic geolocation. 
