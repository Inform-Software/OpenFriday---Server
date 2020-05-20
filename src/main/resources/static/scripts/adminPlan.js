ensureAdmin();

let content = new Vue({
    el: "#content",
    data: {
        slots: [],
        rooms: [],
        workshops: [],
        plan: new Map(),
        unusedWorkshopIDs: []
    },
    computed: {
        unusedWorkshops: function () {
            return this.workshops.filter(ws => $.inArray(ws.id, this.unusedWorkshopIDs) > -1)
        }
    },
    mounted() {
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
                content.rooms = response.data;
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
                    if (jQuery.isEmptyObject(response.data.table)) { // if true, there aren't any workshops in the plan yet
                        content.unusedWorkshopIDs = content.workshops.map(ws => ws.id);
                    }
                    else {
                        content.plan = obj_to_map_deep(response.data.table);
                        for (let i = 0; i < content.workshops.length; i++) {
                            if (!containsWorkshop(content.plan, content.workshops[i].id)) {
                                content.unusedWorkshopIDs.push(content.workshops[i].id);
                            }
                        }

                        // check if there are workshops saved to a room not existing anymore. push them to unused workshops
                        for (let value of content.plan.values()) {
                            for (let [roomID, workshopID] of value.entries()) {
                                if ($.inArray(roomID, content.rooms.map(room => room.id)) < 0) {
                                    content.unusedWorkshopIDs.push(workshopID);
                                    value.delete(roomID);
                                }
                            }
                        }
                    }
                })
        })
    },
    methods: {
        savePlan: function () {
            axios
                .post("/rest/plan/save", {
                    table: map_to_obj(content.plan),
                    unusedWorkshops: content.unusedWorkshopIDs
                })
                .then(function () {
                    alert("Planung gespeichert");
                })
        }
    }
})



// other functions

function dragFromList (ev) {
    // console.log("Start ID: " + ev.target.id)
    ev.dataTransfer.setData("text", ev.target.id);
}

function dragFromTable (ev) {
    ev.dataTransfer.setData("text", ev.target.id + ":" + ev.target.parentElement.id);
}

function dropInTable (ev) {
    ev.preventDefault();

    if (!ev.target.id.includes(":")) {
        // this is the case if the new workshop is dropped on the existing one
        return;
    }

    let keys = ev.target.id.split(':');
    if (content.plan.get(keys[0]).has(keys[1])) {
        return; // do nothing if there is already a workshop at this position; is called if new workshop is dropped in td but not on old workshop in this td
    }

    let data = ev.dataTransfer.getData("text");
    if (!data.includes(":")) { // true if origin of dropped item is the list of unused workshops
        content.unusedWorkshopIDs = content.unusedWorkshopIDs.filter(id => id !== data); // remove workshop from unused workshops

        // content.plan.set(keys[0], content.plan.get(keys[0]).set(keys[1], data)); // add workshop to table
        content.plan.get(keys[0]).set(keys[1], data);
    }
    else {
        data = data.split(':');
        content.plan.get(data[1]).delete(data[2]); // remove workshop from plan
        // content.plan.set(keys[0], content.plan.get(keys[0]).set(keys[1], data[0])); // add workshop to plan
        content.plan.get(keys[0]).set(keys[1], data[0]);
    }
    content.$forceUpdate();
}

function dropInList (ev) {
    ev.preventDefault();
    let data = ev.dataTransfer.getData("text").split(':'); // 0: workshopID, 1: slotID, 2: roomID
    // check if workshop already in list => if yes do nothing
    if (content.unusedWorkshopIDs.filter(id => id === data[0]).length) {
        return;
    }

    let tmp = content.workshops.filter(ws => ws.id === data[0])[0]; // add workshop to unused workshops
    content.unusedWorkshopIDs.push(tmp.id);

    // content.plan.set(data[1], content.plan.get(data[1]).delete(data[2])); // remove workshop from table
    content.plan.get(data[1]).delete(data[2]);
    content.$forceUpdate();
}

function containsWorkshop(nestedMap, workshopID) {
    for (let value of nestedMap.values()) {
        for (let otherValue of value.values()) {
            if (otherValue === workshopID) {
                return true;
            }
        }
    }
    return false;
}