ensureLogin();

const username = getCookie("username");
const userID = getCookie("userID");

let content = new Vue({
    el: "#content",
    data: {
        slots: [],                      // all available slots. Used to build the table
        rooms: [],                      // all available rooms with at least one slot available. Used to build the table
        workshops: [],                  // all workshops. Used to get workshop data (plan only stores to workshop ids)
        plan: new Map(),                // the current plan (which is displayed in the table)
        timestamp: new Date(),          // the timestamp of the current plan
        votes: [],                      // The votes of the current user. Used to color the workshops in the table
        username: username              // make the username accessible in vue expressions in html
    },
    computed: {
        timestamp_string: function () {
            return this.timestamp.getDate() + "." + this.timestamp.getMonth() + "." + this.timestamp.getFullYear() + ", "
                + this.timestamp.getHours() + ":" + String(this.timestamp.getMinutes()).padStart(2, "0");
        }
    },
    mounted() {
        axios
            .post("/rest/user/getvotes/" + userID)
            .then(function (response) {
                content.votes = obj_to_map(response.data);
            })

        let slotPromise = axios
            .post("/rest/slot/getall")
            .then(function (response) {
                content.slots = response.data;
                for (let i = 0; i < content.slots.length; i++) {
                    content.plan.set(content.slots[i].id, new Map());
                }
            })
        let roomPromise = axios
            .post("/rest/room/getall")
            .then(function (response) {
                content.rooms = response.data.filter(room => !jQuery.isEmptyObject(room.slots));
            })
        let workshopPromise = axios
            .post("/rest/workshop/getall")
            .then(function (response) {
                content.workshops = response.data;
            })

        Promise.all([slotPromise, roomPromise, workshopPromise]).then(function () {
            axios
                .post("/rest/plan/get")
                .then(function (response) {
                    content.timestamp = new Date(response.data.timestamp);
                    if (!jQuery.isEmptyObject(response.data.table)) { // if true, there aren't any workshops in the plan yet
                        content.plan = obj_to_map_deep(response.data.table);

                        // check if there are workshops saved to a room not existing anymore. push them to unused workshops
                        for (let value of content.plan.values()) {
                            for (let [roomID, workshopID] of value.entries()) {
                                if ($.inArray(roomID, content.rooms.map(room => room.id)) < 0) {
                                    value.delete(roomID);
                                }
                            }
                        }
                    }
                })
        })
    },
    methods: {
        inArray: function (element, array) {
            return $.inArray(element, array);
        }
    }
})



// other functions

/*
function containsWorkshop(nestedMap, workshopID) {
    for (let value of nestedMap.values()) {
        for (let otherValue of value.values()) {
            if (otherValue === workshopID) {
                return true;
            }
        }
    }
    return false;
}*/
