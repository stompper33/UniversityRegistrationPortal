<?php
$q = intval($_GET['q']);

 $con = mysqli_connect('localhost','root','broncos98','registration');
 if (!$con)
   {
   die('Could not connect: ' . mysqli_error($con));
   }

 mysqli_select_db($con,"registration");
 
 $sql="SELECT cs.course_section_id, CONCAT(c.course_id, ' ', c.course_name) AS course, CONCAT(p.first_name, ' ', p.last_name) AS instructor, cs.day, cs.time, cs.location, cs.start_date, cs.end_date, c.credits FROM professor p, course c, course_section cs, catalog_contains cc WHERE p.uid = cs.instructor_ssn AND cs.course_section_id = cc.course_section_id AND cs.course_id = c.course_id AND cc.catalog_id = '" . $q . "'";

 $result = mysqli_query($con,$sql);

 echo "<table border='1'>
 <tr>
 <th>Section</th>
 <th>Course</th>
 <th>Instructor</th>
 <th>Day</th>
 <th>Time</th>
 <th>Location</th>
 <th>Start date</th>
 <th>End date</th>
 <th>Credits</th>
 </tr>";

 while($row = mysqli_fetch_array($result))
   {
   echo "<tr>";
   echo "<td>" . $row['course_section_id'] . "</td>";
   echo "<td>" . $row['course'] . "</td>";
   echo "<td>" . $row['instructor'] . "</td>";
   echo "<td>" . $row['day'] . "</td>";
   echo "<td>" . $row['time'] . "</td>";
   echo "<td>" . $row['location'] . "</td>";
   echo "<td>" . $row['start_date'] . "</td>";
   echo "<td>" . $row['end_date'] . "</td>";
   echo "<td>" . $row['credits'] . "</td>";
   echo "</tr>";
   }
 echo "</table>";

 mysqli_close($con);
 ?> 