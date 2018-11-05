console.log("Hello");




function grabInput() {
        
    var forminfo = document.getElementById("form");
    var firstname = forminfo.elements[0];
    var lastname = forminfo.elements[1];
    var email = forminfo.elements[2];
    var username = forminfo.elements[3];
    var password = forminfo.elements[4];
    console.log("firstname is : " + firstname.value);
    console.log("lastname is : " + lastname.value);
    console.log("username is : " + username.value);
    console.log("password is : " + password.value);
    console.log("email is : " + email.value);

    
    document.getElementById("demo").innerHTML = forminfo.value;
    $("#img-clck").click(grabInput);
}