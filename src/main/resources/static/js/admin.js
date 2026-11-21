console.log("ADMIN USER ON");

document.querySelector("#image_file_input").addEventListener('change', function(event){
    let file = event.target.files[0];
    if(file){
        let reader = new FileReader();
        reader.onload = function (){
            document.getElementById("upload_image_preview").src= reader.result;
        };
        reader.readAsDataURL(file);
    }
});