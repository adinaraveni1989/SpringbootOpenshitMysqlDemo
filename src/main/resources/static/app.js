(function () {
    const page = document.body.dataset.page;

    function showResult(container, student, heading) {
        container.replaceChildren();
        const title = document.createElement("h2");
        title.textContent = heading || "Student details";
        container.appendChild(title);

        const details = [
            ["Student ID", student.id],
            ["Full name", student.name],
            ["Age", student.age],
            ["Address", student.address],
            ["College", student.college && student.college.name],
            ["College address", student.college && student.college.address],
            ["College ID", student.college && student.college.id]
        ];
        const grid = document.createElement("div");
        grid.className = "result-grid";
        details.forEach(function (item) {
            const card = document.createElement("div");
            card.className = "result-item";
            const label = document.createElement("span");
            label.textContent = item[0];
            const value = document.createElement("strong");
            value.textContent = item[1] == null ? "—" : String(item[1]);
            card.append(label, value);
            grid.appendChild(card);
        });
        container.appendChild(grid);
        container.hidden = false;
    }

    function setMessage(element, text, isError) {
        element.textContent = text;
        element.className = "message " + (isError ? "error" : "success");
        element.hidden = false;
    }

    if (page === "home") {
        const result = document.getElementById("student-result");
        document.getElementById("fetch-form").addEventListener("submit", async function (event) {
            event.preventDefault();
            const id = new FormData(event.currentTarget).get("id");
            result.hidden = true;
            try {
                const response = await fetch("/students/" + encodeURIComponent(id));
                if (response.status === 404) {
                    throw new Error("No student was found with ID " + id + ".");
                }
                if (!response.ok) {
                    throw new Error("Unable to fetch this student (HTTP " + response.status + ").");
                }
                showResult(result, await response.json());
            } catch (error) {
                result.replaceChildren();
                const message = document.createElement("p");
                message.className = "message error";
                message.textContent = error.message;
                result.appendChild(message);
                result.hidden = false;
            }
        });

        document.getElementById("update-form").addEventListener("submit", function (event) {
            event.preventDefault();
            const id = new FormData(event.currentTarget).get("id");
            window.location.href = "/student-form.html?mode=update&id=" + encodeURIComponent(id);
        });
    }

    if (page === "student-form") {
        const query = new URLSearchParams(window.location.search);
        const isUpdate = query.get("mode") === "update";
        const form = document.getElementById("student-form");
        const message = document.getElementById("form-message");
        const savedResult = document.getElementById("saved-student-result");
        const ageInput = document.getElementById("student-age");
        let collegeId = null;

        document.getElementById("form-title").textContent = isUpdate ? "Update student" : "Create student";
        document.getElementById("form-description").textContent = isUpdate
            ? "Review the student and college details. Only students over 18 can be updated."
            : "Enter the student and college details below.";
        document.getElementById("submit-button").textContent = isUpdate ? "Save changes" : "Create student";
        document.getElementById("age-hint").textContent = isUpdate
            ? "Updates are allowed only when age is greater than 18."
            : "Enter the student's age.";

        if (isUpdate) {
            const id = query.get("id");
            if (!id || !/^\d+$/.test(id)) {
                setMessage(message, "Enter a valid student ID from the welcome page.", true);
                form.hidden = true;
            } else {
                fetch("/students/" + encodeURIComponent(id))
                    .then(function (response) {
                        if (response.status === 404) {
                            throw new Error("No student was found with ID " + id + ".");
                        }
                        if (!response.ok) {
                            throw new Error("Unable to load this student (HTTP " + response.status + ").");
                        }
                        return response.json();
                    })
                    .then(function (student) {
                        form.elements.name.value = student.name || "";
                        form.elements.address.value = student.address || "";
                        ageInput.value = student.age;
                        if (student.college) {
                            collegeId = student.college.id;
                            form.elements.collegeName.value = student.college.name || "";
                            form.elements.collegeAddress.value = student.college.address || "";
                        }
                    })
                    .catch(function (error) {
                        setMessage(message, error.message, true);
                        form.hidden = true;
                    });
            }
        }

        form.addEventListener("submit", async function (event) {
            event.preventDefault();
            if (isUpdate && Number(ageInput.value) <= 18) {
                setMessage(message, "Student updates are allowed only when age is greater than 18.", true);
                return;
            }
            const values = new FormData(form);
            const college = {
                name: values.get("collegeName"),
                address: values.get("collegeAddress")
            };
            if (isUpdate && collegeId != null) {
                college.id = collegeId;
            }
            const student = {
                name: values.get("name"),
                address: values.get("address"),
                age: Number(values.get("age")),
                college: college
            };
            const id = query.get("id");
            try {
                const response = await fetch(isUpdate ? "/students/" + encodeURIComponent(id) : "/students", {
                    method: isUpdate ? "PUT" : "POST",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(student)
                });
                if (response.status === 403) {
                    throw new Error("Student updates are allowed only when age is greater than 18.");
                }
                if (response.status === 404) {
                    throw new Error(isUpdate ? "Student or college not found." : "The selected college was not found.");
                }
                if (!response.ok) {
                    throw new Error("Unable to save student (HTTP " + response.status + ").");
                }
                const savedStudent = await response.json();
                setMessage(message, isUpdate ? "Student details updated successfully." : "Student created successfully.", false);
                showResult(savedResult, savedStudent, "Saved student");
            } catch (error) {
                setMessage(message, error.message, true);
            }
        });
    }
}());
