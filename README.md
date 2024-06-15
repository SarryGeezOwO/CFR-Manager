# Configuration For Rascals
> a custom file .cfr, made by me. It follows a tree style strict formatting.<br>
> Note : Comments are still not supportted<br>

<h3>CFR has 2 components :</h3>
# <b>Holder</b> : A holder is a container for unique properties<br>
# <b>Property</b> : A property is a key-value pair, Keys are unique per holders; Keys can't have a duplicate in a holder

## Format for CFR files :
<pre>
Holder1 {
  Key1 : Value
  Key2 : Value
}
Holder2 {
  Key1 : Value
  Key2 : Value
}
</pre>

When writing CFR manually 
These spacing are required :
<pre>
HolderName {       // That one space between the name and '{' is important!
  Key : Value      // " : " is important spacing!
}                  // Always end a holder with a closing curly brace '}'
</pre>


## CFR Handler Operations :
* Create Holder
* Create Property
* Read Holder
* GetProperty
* GetPropertyValue
* UpdateProperty
* DeleteHolder
* DeleteProperty
