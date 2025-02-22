function getPostId() {
    return document.querySelector("meta[name='post-id']").getAttribute("content");
}

function getRoot() {
    return document.querySelector("meta[name='root']").getAttribute("content");
}

function likePost() {
    const postId = getPostId();
    const root = getRoot();
    fetch(`${root}posts/${postId}/like`, {
        method: "GET"
    })
        .then((num) => {
            num.text().then(value => {
                document.querySelector(".like-count em").innerText = value;
            });

        })
        .catch(error => console.error("Error removing comment:", error));
}

function removeComment(commentId) {
    const postId = getPostId();
    const root = getRoot();
    fetch(`${root}posts/${postId}/comments/${commentId}`, {
        method: "delete"
    })
        .then(() => {
            const commentElement = document.getElementById(`comment-${commentId}`);
            const liElement = commentElement.closest('LI');
            liElement.remove();
            window.location.reload();
        })
        .catch(error => console.error("Error removing comment:", error));
}

function previewImage(event) {
    const file = event.target.files[0];
    if (file) {
        const reader = new FileReader();
        reader.onload = function (e) {
            document.getElementById("edit-image-preview").src = e.target.result;
        };
        reader.readAsDataURL(file);
    }
}

function addCommentPopup() {
    document.getElementById("comment-popup").style.display = "block";
}

function updateCommentPopup(commentId, commentNode) {
    const commentTextarea = document.getElementById("new-comment");
    if (commentNode != null) {
        commentTextarea.value = commentNode.innerHTML;
    }
    if (commentId != null) {
        commentTextarea.setAttribute("current_comment_id", commentId);
    }
    document.getElementById("comment-popup").style.display = "block";
}

function closePopup(elementId) {
    document.getElementById(elementId).style.display = "none";
}

function closeCommentPopup() {
    document.getElementById("new-comment").value = ""; // Clear the textarea
    document.getElementById("comment-popup").style.display = "none";
}

function saveComment() {
    let commentNode = document.getElementById("new-comment");
    const commentText = commentNode.value.trim();
    let currentCommentId = commentNode.getAttribute("current_comment_id");
    if (!commentText && currentCommentId < 1) {
        alert("Comment cannot be empty!");
        closeCommentPopup();
        return;
    }
    const postId = getPostId();
    const root = getRoot();
    const url = currentCommentId ? `${root}posts/${postId}/comments/${currentCommentId}` : `${root}posts/${postId}/comments`;

    const method = 'post';
    const formData = new FormData();
    formData.append("content", commentText);

    fetch(url, {
        method: method,
        body: formData
    })
        .then(() => {
            closeCommentPopup();
            // just reload the page
            window.location.href = `${root}posts/${postId}`;
        })
        .catch(error => console.error("Error saving comment:", error));
}

document.addEventListener("DOMContentLoaded", () => {
    const page = document.body.getAttribute("id");

    switch (page) {
        case "feed-page":
            // Attach event listener to the "Add Post" button
            document.querySelector(".add-post-btn").addEventListener("click", (event) => {
                document.getElementById("add-post-popup").style.display = "block";
            });

            // Attach event listener to the "Cancel" button inside the Add post popup
            document.querySelector("#add-post-popup .cancel-btn").addEventListener("click", () => {
                closePopup("add-post-popup");
            });

            break;

        case "post-page":
            // Attach event listener to the "Edit Post" button
            document.querySelector("#single-post .edit-post-btn").addEventListener("click", (event) => {
                document.getElementById("edit-post-popup").style.display = "block";
            });

            // Attach event listener to the "Edit Post" button
            document.querySelector("#single-post .like-post-btn").addEventListener("click", (event) => {
                likePost();
            });

            // Attach event listener to the "Add Comment" button
            document.querySelector(".add-comment-btn").addEventListener("click", (event) => {
                addCommentPopup();
            });

            // Attach event listener to the "Save Comment" button
            document.querySelector("#comment-popup .save-btn").addEventListener("click", (event) => {
                saveComment(event);
            });

            // Attach event listener to the "Cancel" button inside the popup
            document.querySelector("#edit-post-popup .cancel-btn").addEventListener("click", () => {
                closePopup("#edit-post-popup");
            });
            // Attach event listener to the "Cancel" button inside the popup
            document.querySelector("#comment-popup .cancel-btn").addEventListener("click", () => {
                closeCommentPopup();
            });
            // Add event listener for the comment list container
            let comments = document.getElementById("comment-list");
            if (comments != null)
            {
                comments.addEventListener("click", (event) => {
                    const target = event.target;
                    const commentId = event.target.parentElement.id.split("-")[1];
                    if (event.target.nodeName === "DIV") {
                        updateCommentPopup(commentId, target);
                    } else if (target.tagName === "BUTTON") {
                        removeComment(commentId);
                    }
                });
            }

            break;
    }
});



