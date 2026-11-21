console.log("Script js Working");

const viewContactModal = document.getElementById("view_contact_modal");
const base_url="http://localhost:8080";
// options with default values
const options = {
    placement: 'bottom-right',
    backdrop: 'dynamic',
    backdropClasses:
        'bg-gray-900/50 dark:bg-gray-900/80 fixed inset-0 z-40',
    closable: true,
    onHide: () => {
        console.log('modal is hidden');
    },
    onShow: () => {
        console.log('modal is shown');
    },
    onToggle: () => {
        console.log('modal has been toggled');
    },
};
// instance options object
const instanceOptions = {
    id: 'view_contact_modal',
    override: true
};

const contactModal = new Modal(viewContactModal, options, instanceOptions);

function openContactModal(){
    contactModal.show();
}

function closeContactModal(){
    contactModal.hide();
}

async function loadContactData(id){
    console.log(id);
    try{
        const result = await(
            await fetch(`${base_url}/api/contacts/${id}`)
            ).json();
        console.log(result);

        document.querySelector('#contact_name').innerHTML = result.name || "N/A";
        document.querySelector('#contact_email').innerHTML = result.email || "No Email";
        document.querySelector('#contact_phone').innerHTML ="+91 "+ result.phoneNumber || "No Phone Number";
        document.querySelector('#contact_image').src = result.picture || "https://as1.ftcdn.net/v2/jpg/00/64/67/52/1000_F_64675209_7ve2XQANuzuHjMZXP3aIYIpsDKEbF5dD.jpg";
        document.querySelector('#contact_address').innerHTML = result.address || "No Address Provided";
        document.querySelector('#contact_description').textContent = result.description || "No Description";
        document.querySelector('#contact_website').href = result.socialLinks || "#";
        document.querySelector('#contact_website').textContent = result.socialLinks || "No Link Available";
        document.querySelector('#contact_linkdin').textContent = result.linkdin || "No Link Available";
        document.querySelector('#contact_linkdin').href = result.linkdin || "#";

        if(result.favorite){
            document.querySelector('#contact_favorite').innerHTML = `<span class="text-sm text-green-700">Favorite</span>`;
        }
        else{
            document.querySelector('#contact_favorite').innerHTML = `<span class="text-sm text-red-700">Not Favorite</span>`;
        }
        openContactModal();
    }
    catch (error){
        console.log(error);
    }
}


//delete Contact
async function deleteContact(id){
    Swal.fire({
        title: "Do you want to delete the contact?",

        showCancelButton: true,
        confirmButtonText: "Delete"

    }).then((result) => {
        /* Read more about isConfirmed, isDenied below */
        if (result.isConfirmed){

            const url = `${base_url}/user/contacts/delete/${id}`;
            window.location.replace(url);

            //Swal.fire("Contact deleted successfully!", "", "success");
        }
        //else if (result.isDenied) Swal.fire("Changes are not saved", "", "info");
    });
}