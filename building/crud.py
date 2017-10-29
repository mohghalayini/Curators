# Copyright 2015 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

from building import get_model
from flask import Blueprint, redirect, render_template, request, url_for


crud = Blueprint('crud', __name__)


# [START list]
# Executed on the entry of the home page
@crud.route("/")
def list():
    token = request.args.get('page_token', None)
    if token:
        token = token.encode('utf-8')

    # Load all the rooms saved into a dictionary
    rooms, next_page_token = get_model().list(cursor=token)

    # Render the html file with the data needed
    return render_template(
        "list.html",
        rooms=rooms,
        next_page_token=next_page_token)
# [END list]

# Executed on the access of room details
@crud.route('/<id>')
def view(id):
    # Read the room chosen from the datastore
    room = get_model().read(id)
    return render_template("view.html", room=room)


# [START add]
# Executed upon entry of add page
@crud.route('/add', methods=['GET', 'POST'])
def add():
    # Handle post requests by inseting the information into the datastore
    if request.method == 'POST':
        # Load the data inserted by the user in the form into a dictionary
        data = request.form.to_dict(flat=True)

        # Format the counter and capacity to be stored in three digits format 
        # and handle empty entry        
        if data['counter'] == "" or int(data['counter'])<0:
            data['counter'] = format(0, '03d')
            
        else:
            if int(data['counter']) > int(data['capacity']):
               data['counter'] = format(int(data['capacity']), '03d')
            else:
               data['counter'] = format(int(data['counter']), '03d')      
           
        if data['capacity'] == "" or int(data['capacity'])<0: 
            data['capacity'] = format(0,'03d')
        else:
            data['capacity'] = format(int(data['capacity']),'03d')

        # Send data to the datastore
        room = get_model().create(data)
        
        # Go to the view page displaying the inserted room 
        return redirect(url_for('.view', id=room['id']))

    # Display the form in the case of a GET request
    return render_template("form.html", action="Add", room={})
# [END add]

# [START edit]
# Executed upon clicking the edit button
@crud.route('/<id>/edit', methods=['GET', 'POST'])
def edit(id):

    # Read old data stored in datastore for the room to be edited
    room = get_model().read(id)
    
    # Handle POST requests
    if request.method == 'POST':
    
        # Load the data inserted by the user in the form into a dictionary
        data = request.form.to_dict(flat=True)

  
        # Update the capacity value
        if data['capacity'] != "": 
            data['capacity'] = format(int(data['capacity']),'03d')    
        else:
            data['capacity'] = format(int(room['capacity']),'03d')
            
        # Increment the counter value by the inserted value        
        if data['counter'] != "":
            newCounter = int(data ['counter']) + int(room['counter'])
            
            if newCounter > int(data['capacity']):
                data ['counter'] = format(int(data['capacity']), '03d')
            elif newCounter < 0:
                data ['counter'] = format(0, '03d')
            else:
                data ['counter'] = format(newCounter, '03d')    
        else:
            data['counter'] = format(int(room['counter']), '03d')
        
        # Send updated data to the datastore
        room = get_model().update(data, id)

        # Go to the view page displaying the inserted room 
        return redirect(url_for('.view', id=room['id']))

    # Display the form in the case of a GET request 
    return render_template("form.html", action="Edit", room=room)
# [END edit]

# Executed upon the click of the delete button
@crud.route('/<id>/delete')
def delete(id):
    get_model().delete(id)
    return redirect(url_for('.list'))
