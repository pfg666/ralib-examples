function stNew = main_v2 (state, direction, laneChangeRequest, b1, b2, laneChngReq, b4, b5, b6)
%st contain 
%         state, direction, laneChangeRequest, b1, b2, laneChngReq, b4, b5, b6
    %procedure updateState
    %one parameter for duringStateB abstracted away
    
    % all the comment should be 1 ann not 0, should be considered both the cases
     
     %in the following lines convert the integers inputs in acceptable valures from the logic of the system that should not be changed.
     
     if state == 0
         mode.state = 'stateA';
     elseif state == 1
         mode.state = 'stateB';
     else
     	 mode.state = 'NDS';
     end
     
     if direction == 1 
         mode.direction = 'left';
     elseif direction == 2
         mode.direction = 'right';
     else 
         mode.direction = 'none';
     end
     
     if laneChangeRequest == 0
         mode.laneChangeRequest = false;
     else % elseif laneChangeRequest == 0  
         mode.laneChangeRequest = true;
     end
     
     if b1 == 0
         mode.b1 = false;
     else % elseif b1 == 0
         mode.b1 = true;
     end
     
     if b2 == 0
         mode.b2 = false;
     else % elseif b2 == 0 
         mode.b2 = true;
     end
     
     if laneChngReq == 1 
         laneChngReq = 'left';
     elseif laneChngReq == 2
         laneChngReq = 'right';
     else 
         laneChngReq = 'none';
     end
     
     if b4 == 1
         decision_var.b4 = true ;
     else % elseif b4 == 0 
         decision_var.b4 = false;
     end
     
     if b5 == 1
         decision_var.b5 = true;
     else % elseif b5 == 0 
         decision_var.b5 = false;
     end
     
     if b6 == 1
         decision_var.b6 = true;
     else % elseif b6 == 1 
         decision_var.b6 = false;       
     end

    %PROFILE_START OP_UPDATE_STATE
    if ~strcmp(laneChngReq,'none') % if we have request laneChangeRequest is true
        mode.laneChangeRequest = true;
    else
        mode.laneChangeRequest = false;
    end
    
    switch mode.state
      case 'stateA',
            mode = duringStateA(mode, laneChngReq);     
      case 'stateB',     
            mode = duringStateB(mode, decision_var);
      case 'NDS',
      	    stNew.state = 2;
     % case 'NDS'
     %       mode = errorState(mode, decision_var);
    end
    %PROFILE_END OP_UPDATE_STATE

     if strcmp(laneChngReq,'none')
         stNew.laneChngReq = 0;
     elseif strcmp(laneChngReq,'left')
         stNew.laneChngReq = 1;
     elseif strcmp(laneChngReq,'right')
         stNew.laneChngReq = 2;
     end
      
     if strcmp(mode.state,'stateA')
         stNew.state = 0;
     elseif strcmp(mode.state,'stateB')
         stNew.state = 1;
     else 
     	 stNew.state = 2;
     end
     
     if strcmp(mode.direction,'none')
         stNew.direction = 0;
     elseif strcmp(mode.direction,'left')
         stNew.direction = 1;
     elseif strcmp(mode.direction,'right')
         stNew.direction = 2;
     end
     
     if mode.laneChangeRequest == false
         stNew.laneChangeRequest = 0;
     else
         stNew.laneChangeRequest = 1;
     end
     
     if mode.b1 == false
         stNew.b1 = 0;
     else
         stNew.b1 = 1;
     end
     
      if mode.b2 == false
         stNew.b2 = 0;
     else
         stNew.b2 = 1;
     end
     
     if decision_var.b4 == true
         stNew.b4 = 1;
     else
         stNew.b4 = 0;
     end
     
     if decision_var.b5 == true
         stNew.b5 = 1;
     else
         stNew.b5 = 0;
     end
     
     if decision_var.b6 == true
         stNew.b6 = 1;
     elseif decision_var.b6 == false
         stNew.b6 = 0;
     end
end
  
function [mode] = enterStateA(mode)
    mode.state = 'stateA';
    mode.direction = 'none';
    %mode.duration = 0;      
end

function [mode] = duringStateA(mode, laneChngReq)
    mode.direction = laneChngReq;
    mode.b2 = false;
    mode.b1 = false;
    	if ~strcmp(laneChngReq,'none') %laneChngReq ~= 'none'
    	    mode.state = 'stateB';
    	else %if there is no request to change line stay in A
            mode.state = 'stateA';
    	end 
end
function [mode] = enterStateB(mode)
	mode.state = 'stateB';
end


function [mode] = duringStateB(mode, decision_var)
    currentb2State = mode.b2;
    mode.b2 = currentb2State;
    mode.b1 = true;
    
    %get parameter for decision_var.b3
    
    %arithmetic for decision_var.b6: removed one argument from duringStateB that is used here
    
    %function call for decision_var.b4

    %function call for decision_var.b5
            
    if ~mode.laneChangeRequest %if no request is false back to A 
        mode = enterStateA(mode);    
    elseif decision_var.b4
        if decision_var.b5
           mode = enterStateB(mode);
        end
    %combining some OR logic to one decision_var.b6    
    elseif decision_var.b6
        mode = enterStateB(mode);
    end
end

%function [mode] = errorState(mode, decision_var)
%    mode.state = 'NDS';
%end

