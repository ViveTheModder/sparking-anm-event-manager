# sparking-anm-event-manager
A modding tool for the PS2 (and Wii, though untested) version of DBZ Budokai Tenkaichi 2 & 3 that is capable of **adding and deleting events** from animations,
as well as **editing the flags of said events**.

**Flags** refer to **parameters that are enabled** within an event.

An event refers to a given **keyframe during which the flags are activated**.

The first version of this program went by the name "Tenkaichi Basic ANM Editor", the development of which took place in April 2025.
Unfortunately, because of my frustrations from writing the user interface code before the functionality, it had to be abandoned.

Recently, those frustrations have shifted towards vibecoded modding tools, made by grifters/larpers who lack any programming knowledge or interest.
As such, spite served as a powerful (though short-lived) motivator to remake the program.

# Demonstration
**DISCLAIMER:** Most of the **event names** were taken from [HiroTex](https://github.com/HiroTex)'s research included in [Sparking Studio](https://mega.nz/file/OYM3TQrQ#Sq8_IoOpFI30vF4dDr0R0-JmpQbaseY0fQgsBGjiWxk). As for the **ANM template** (different bytes highlighted in different colors) shown in 010 Editor, it was made by [Ascomods](https://github.com/ascomods).
## Functionality
### Traversing Events

<img width="1136" height="1016" alt="image" src="https://github.com/user-attachments/assets/bb86603c-ff14-491a-8ffe-faff7c58c0a3" />

<img width="1136" height="1016" alt="image" src="https://github.com/user-attachments/assets/a909c157-9349-4566-b5e0-8946920a2b5e" />

<img width="1136" height="1016" alt="image" src="https://github.com/user-attachments/assets/c0f98aaf-6849-49fd-b7ba-654c84206e91" />

### Adding New Event
Once ``Add Event`` is clicked, a new event is inserted, with the keyframe being that of the previous event but incremented by one.

<img width="566" height="331" alt="image" src="https://github.com/user-attachments/assets/90a34904-cee7-4b65-b169-753086e59eec" />

### Removing Existing Event
Once ``Remove Event`` is clicked, the bytes of the selected event are immediately deleted from the file.

<img width="573" height="331" alt="image" src="https://github.com/user-attachments/assets/babd2317-d15f-4d8d-a65d-1f230fbff419" />

<img width="463" height="81" alt="image" src="https://github.com/user-attachments/assets/7d20c0b3-7a90-41fc-9f8e-5b41cb4abe29" />

<img width="557" height="310" alt="image" src="https://github.com/user-attachments/assets/b11d3eb0-e6fa-46d1-9472-ad52d06a2f79" />

### Editing Flags of Any Event

<img width="1136" height="1016" alt="image" src="https://github.com/user-attachments/assets/df38eadc-f85e-4d4a-b683-768aa6dc22c5" />

<img width="551" height="309" alt="image" src="https://github.com/user-attachments/assets/6a0288b5-3da1-44cd-bd6f-d4b2ff221c0c" />

### Editing Event Keyframe and Animation Duration
Though not stated in the tooltip, editing either the keyframe or the duration requires ``Apply Events`` to be clicked, for the changes to be saved to the ANM.

<img width="385" height="34" alt="image" src="https://github.com/user-attachments/assets/a7b4d844-0ba2-4f93-9460-cd286eca75fd" />

<img width="557" height="310" alt="image" src="https://github.com/user-attachments/assets/f136cbea-9380-4f45-b790-fc6f82fd8992" />

<img width="489" height="45" alt="image" src="https://github.com/user-attachments/assets/afc3e2d1-91fa-4fc1-b0e2-8a0a90e77c36" />

<img width="554" height="310" alt="image" src="https://github.com/user-attachments/assets/b37bf216-e134-4d3d-acbe-b89edba78e7d" />

## Themes
<img width="1136" height="1016" alt="image" src="https://github.com/user-attachments/assets/f8e487d0-b15f-4bc8-9a96-d484c5f83267" />

<img width="1136" height="1016" alt="image" src="https://github.com/user-attachments/assets/00c3c76e-3c6f-4bbf-b6ec-4cc204c0c514" />

<img width="1136" height="1016" alt="image" src="https://github.com/user-attachments/assets/d3b6e6a4-4358-42d5-955a-e552329a05af" />

<img width="1136" height="1016" alt="image" src="https://github.com/user-attachments/assets/cb20254e-e040-4e25-9f44-3d83d66dbc8e" />

<img width="1136" height="1016" alt="image" src="https://github.com/user-attachments/assets/f80d87b3-d37e-4f1c-bc2b-61ed4ca2711c" />

<img width="1136" height="1016" alt="image" src="https://github.com/user-attachments/assets/1e074a37-2825-4411-978c-c9f2afb975bd" />

## Tooltips
<img width="548" height="73" alt="image" src="https://github.com/user-attachments/assets/d279fb15-9329-4ace-bf31-b076a924f235" />

<img width="426" height="89" alt="image" src="https://github.com/user-attachments/assets/7a7ca28b-28d6-4225-861e-1d4cfe3d20bc" />

<img width="698" height="81" alt="image" src="https://github.com/user-attachments/assets/ce6dec7b-18ae-45d2-9879-7126719b4cf4" />

<img width="381" height="82" alt="image" src="https://github.com/user-attachments/assets/d04d49d7-35af-42ce-981b-10496597c2c5" />

<img width="310" height="117" alt="image" src="https://github.com/user-attachments/assets/6534d814-34cc-449b-b58f-c628894c25cf" />

<img width="495" height="50" alt="image" src="https://github.com/user-attachments/assets/0407d665-ca49-410a-858f-261681052f2a" />

<img width="595" height="68" alt="image" src="https://github.com/user-attachments/assets/4f24fe37-c9f0-4686-87e3-5fe6f2361ba7" />

<img width="521" height="41" alt="image" src="https://github.com/user-attachments/assets/7fa4a44f-4962-44c0-befd-468d45e25e83" />


