ensureLogin();

const user = JSON.parse(getCookie("user"));

let content = new Vue({
    el: "#content",
    data: {
        plan: {
            workshops: [],
            timeslots: [],
            rooms: []
        },
        votes: [],                      // The votes of the current user. Used to color the workshops in the table
        username: user.name             // make the username accessible in vue expressions in html
    },
    computed: {
/*        timestamp_string: function () {
            return this.timestamp.getDate() + "." + this.timestamp.getMonth() + "." + this.timestamp.getFullYear() + ", "
                + this.timestamp.getHours() + ":" + String(this.timestamp.getMinutes()).padStart(2, "0");
        }*/
    },
    mounted() {
        axios
            .post("/rest/user/getvotes/" + user.id)
            .then(function (response) {
                content.votes = response.data;
            })

        axios
            .post("/rest/plan/get")
            .then(function (response) {
                content.plan = response.data;
                content.plan.workshops = content.plan.workshops.filter(ws => ws.room && $.inArray(ws.room.id, content.plan.rooms.map(r => r.id)) > -1);     // remove workshops without assigned room or with room assigned which was deleted
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
