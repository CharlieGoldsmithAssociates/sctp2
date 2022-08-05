update enrollment_sessions
   set mobile_review = 1
 where status = 'review' and mobile_review = 0
;